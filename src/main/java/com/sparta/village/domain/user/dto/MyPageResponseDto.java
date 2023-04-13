package com.sparta.village.domain.user.dto;

import com.sparta.village.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyPageResponseDto {
    private String nickname;
    private String profile;

    private List<?> myList;

    public MyPageResponseDto(User user, List<?> myList) {
        this.nickname = user.getNickname();
        this.profile = user.getProfile();
        this.myList = myList;
    }
}
