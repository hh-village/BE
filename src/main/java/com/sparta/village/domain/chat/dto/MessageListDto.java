package com.sparta.village.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageListDto {
    private String sender;
    private String content;
    private String roomId;
}
