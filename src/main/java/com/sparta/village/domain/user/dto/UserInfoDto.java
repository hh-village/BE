package com.sparta.village.domain.user.dto;

import lombok.Getter;

@Getter
public class UserInfoDto {
    private Long kakaoId;


    public UserInfoDto(Long kakaoId){

        this.kakaoId = kakaoId;
    }
}
