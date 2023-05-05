package com.sparta.village.domain.chat.repository;

import com.sparta.village.domain.chat.dto.MessageListDto;
import com.sparta.village.domain.chat.dto.MyChatRoomResponseDto;
import com.sparta.village.domain.chat.dto.RoomListDto;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public class ChatMessageQueryRepository {
    private final EntityManager em;

    public ChatMessageQueryRepository(EntityManager em)
    {
        this.em = em;
    }

    public MyChatRoomResponseDto findMessageList(Long roomId, Long userId) {
        if (roomId == null) {
            String sqlGetRoomId = "select r.id from chat_room r " +
                    "left join chat_message m on r.id = m.room_id " +
                    "where r.user_id = :userId or r.owner_id = :userId " +
                    "order by m.id desc " +
                    "limit 1";

            Query queryGetRoomId = em.createNativeQuery(sqlGetRoomId);
            queryGetRoomId.setParameter("userId", userId);
            BigInteger resultsGetRoomId = (BigInteger) queryGetRoomId.getSingleResult();
            roomId  = resultsGetRoomId.longValue();
        }

        String sqlGetMessageList = "select users.nickname, m.content, chat_room.id, m.created_at " +
                "from chat_message m " +
                "left join users on m.sender_id = users.id " +
                "left join chat_room on m.room_id = chat_room.id " +
                "where m.room_id = :roomId";

        Query queryMessageList = em.createNativeQuery(sqlGetMessageList);
        queryMessageList.setParameter("roomId", roomId);
        List<Object[]> resultsMessageList = queryMessageList.getResultList();
        List<MessageListDto> messageList = resultsMessageList.stream().map(m -> new MessageListDto((String)m[0], (String)m[1], Long.parseLong(m[2].toString()), changeDateFormat((String)m[3]))).toList();

        String sqlGetRoomList = "SELECT r.id, " +
                "case when r_user.id = :userId then r_owner.nickname else r_user.nickname end as nickname, " +
                "case when r_user.id = :userId then r_owner.profile else r_user.profile end as profile, " +
                "m.content, " +
                "case when r.id = :roomId" +
                " then true else false end as target " +
                "FROM chat_room r " +
                "LEFT JOIN (" +
                "    SELECT room_id, MAX(id) as max_id " +
                "    FROM chat_message " +
                "    GROUP BY room_id " +
                ") as latest_message " +
                "ON r.id = latest_message.room_id " +
                "LEFT JOIN chat_message m ON latest_message.max_id = m.id " +
                "left join product p on p.id = r.product_id " +
                "LEFT JOIN users r_user ON r.user_id = r_user.id " +
                "LEFT JOIN users r_owner ON r.owner_id = r_owner.id " +
                "WHERE (r.owner_id = :userId or r.user_id = :userId) " +
                "AND r.is_deleted = false " +
                "ORDER BY m.id DESC";



        Query queryRoomList = em.createNativeQuery(sqlGetRoomList);
        queryRoomList.setParameter("userId", userId);
        queryRoomList.setParameter("roomId", roomId);
        List<Object[]> resultsRoomList = queryRoomList.getResultList();
        List<RoomListDto> roomList = resultsRoomList.stream().map(r -> new RoomListDto(Long.parseLong(r[0].toString()), (String)r[1], (String)r[2], (String)r[3], Integer.parseInt(String.valueOf(r[4])) == 1)).toList();

        return new MyChatRoomResponseDto(messageList, roomList);
    }

    private String changeDateFormat(String createdAt) {
        String[] date = createdAt.split(" ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String today = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
        return date[0].equals(today) ? date[1] : date[0];
    }
}
