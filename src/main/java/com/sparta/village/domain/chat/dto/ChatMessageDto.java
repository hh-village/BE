package com.sparta.village.domain.chat.dto;

import com.sparta.village.domain.chat.entity.ChatMessage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDto {
    private ChatMessage.MessageType type; // 메시지 타입
    private String roomId; // 방번호
    private String sender; // nickname
    private String content; // 메시지


}
