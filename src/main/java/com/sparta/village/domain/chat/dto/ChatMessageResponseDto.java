package com.sparta.village.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDto {
    private String target;

    private List<MessageListDto> messageList;

    private List<RoomListDto> roomList;

}
