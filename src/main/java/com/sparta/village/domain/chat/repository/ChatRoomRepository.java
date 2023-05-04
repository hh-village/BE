package com.sparta.village.domain.chat.repository;

import com.sparta.village.domain.chat.entity.ChatRoom;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomId(String roomId);

    Optional<ChatRoom> findChatRoomByProductAndUser(Product product, User user);

    @Modifying
    @Query(value = "update chat_room r " +
            "left join chat_message on r.id = chat_message.room_id " +
            "set r.is_deleted = true, " +
            "chat_message.is_deleted = true " +
            "where r.id = :id", nativeQuery = true)
    void deleteAllAboutRoomById(@Param("id") Long id);
}
