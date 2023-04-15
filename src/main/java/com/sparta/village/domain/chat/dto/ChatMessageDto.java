package com.sparta.village.domain.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDto {
    private String roomId; // 방번호
    private String sender; // nickname
    private String content; // 메시지
}
