package com.sparta.village.domain.chat.entity;

import com.sparta.village.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User sender;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private ChatRoom room;

    @Column(nullable = false)
    private MessageType type;

    public enum MessageType {
        ENTER, TALK, QUIT
    }

    public ChatMessage(User sender, String content, ChatRoom room, MessageType messageType) {
        this.sender = sender;
        this.content = content;
        this.room = room;
        this.type = messageType;
    }
}
