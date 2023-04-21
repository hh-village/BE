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
    private String lastMessage;
    private boolean target;

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (o == this)
            return true;

        if (o.getClass() != getClass())
            return false;
        RoomListDto r = (RoomListDto) o;
        return this.nickname.equals(r.getNickname()) &&
                this.profile.equals(r.getProfile()) &&
                this.roomId.equals(r.getRoomId()) &&
                this.lastMessage.equals(r.getLastMessage()) &&
                this.isTarget() == r.isTarget();
    }
}
