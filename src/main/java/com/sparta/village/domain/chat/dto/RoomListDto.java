package com.sparta.village.domain.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomListDto {
    private String roomId;
    private String nickname;
    private String profile;
}
