package com.sparta.village.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.village.domain.user.service.KakaoUserService;
import com.sparta.village.global.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final KakaoUserService kakaoUserService;

    @GetMapping("/users/login")
    public ResponseEntity<ResponseMessage> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // code: 프론트 엔드로부터 받은 인가 코드
        return kakaoUserService.kakaoLogin(code, response);


    }
}

