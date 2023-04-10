package com.sparta.village.domain.chat.entity;

import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomId;

    @ManyToOne
    private Product product;
    @ManyToOne
    private User user;
    @ManyToOne
    private User owner;

    public ChatRoom(Product product, User user, User owner) {
        this.roomId = UUID.randomUUID().toString().substring(0, 8);
        this.product = product;
        this.user = user;
        this.owner = owner;
    }
}
