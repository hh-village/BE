package com.sparta.village.domain.chat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.village.domain.reservation.entity.Timestamped;
import com.sparta.village.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User sender;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    private ChatRoom room;

    @Column(nullable = false)
    private String createdAt;

    @PrePersist
    private void setCreatedAt() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.createdAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
    }

    public ChatMessage(User sender, String content, ChatRoom room) {
        this.sender = sender;
        this.content = content;
        this.room = room;
    }
}
