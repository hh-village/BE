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

    @Query(value = "select r from ChatRoom r where r.user = :user or r.owner = :user")
    List<ChatRoom> findAllChatRoomByUser(@Param("user") User user);

    @Modifying
    @Query("DELETE FROM ChatRoom cr WHERE cr.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);
}
