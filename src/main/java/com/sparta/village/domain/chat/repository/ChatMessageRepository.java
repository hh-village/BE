package com.sparta.village.domain.chat.repository;

import com.sparta.village.domain.chat.entity.ChatMessage;
import com.sparta.village.domain.chat.entity.ChatRoom;
import com.sparta.village.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findAllByRoom(ChatRoom room);

    @Query(value = "select r.room_id from chat_room r left join chat_message m on r.id = m.room_id where r.user_id = :userId or r.owner_id = :userId order by m.id desc;", nativeQuery = true)
    List<String> findRoomIdOfLastChatMessage(@Param("userId") Long userId);


    @Modifying
    @Query("DELETE FROM ChatMessage cm WHERE cm.room.id IN (SELECT cr.id FROM ChatRoom cr WHERE cr.product.id = :productId)")
    void deleteMessagesByProductId(@Param("productId") Long productId);

    @Query("select m from ChatMessage m where m.room = :room order by m.id desc")
    List<ChatMessage> findLastMessageByRoom(@Param("room") ChatRoom room);
}
