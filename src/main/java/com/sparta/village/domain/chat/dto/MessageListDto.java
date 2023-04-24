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
    private String createdAt;

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (o == this)
            return true;

        if (o.getClass() != getClass())
            return false;

        MessageListDto m = (MessageListDto) o;

        return this.getSender().equals(m.getSender()) &&
                this.getContent().equals(m.getContent()) &&
                this.getRoomId().equals(m.getRoomId());
    }
}
