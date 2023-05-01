package com.sparta.village.domain.chat.entity;

import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "update chat_room set is_deleted = true where id = ?")
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

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<ChatMessage> chatMessageList;
    private boolean isDeleted = Boolean.FALSE;

    public ChatRoom(Product product, User user, User owner) {
        this.roomId = UUID.randomUUID().toString().substring(0, 8);
        this.product = product;
        this.user = user;
        this.owner = owner;
    }
}
