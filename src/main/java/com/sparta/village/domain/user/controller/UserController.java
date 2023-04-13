package com.sparta.village.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.village.domain.user.dto.NicknameRequestDto;
import com.sparta.village.domain.user.service.KakaoUserService;
import com.sparta.village.domain.user.service.UserService;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@RestController
@RequiredArgsConstructor
public class UserController {

    private final KakaoUserService kakaoUserService;

    private final UserService userService;

    @GetMapping("/users/login")
    public ResponseEntity<ResponseMessage> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        // code: 프론트 엔드로부터 받은 인가 코드
        return kakaoUserService.kakaoLogin(code, response);

    }

    // 사용자의 닉네임을 업데이트 하기 위한 patch매핑
    @PatchMapping("/users")
    public ResponseEntity<ResponseMessage> updateNickname
        // 요청 본문에서 새 닉네임 정보를 가져오기 위한 NicknameRequestDto 객체
        (@RequestBody NicknameRequestDto requestDto,
         // 현재 인증된 사용자의 정보를 가져오기 위한 UserDetailsImpl 객체
         @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // userService의 updateNickname 메소드를 호출하여 닉네임을 업데이트하고, 결과를 반환
        return userService.updateNickname(requestDto.getNickname(), userDetails.getUser());
    }

    @GetMapping("/users")
    public ResponseEntity<ResponseMessage> getUsersItems(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam(required = false) String key) {
        return userService.getUsersItems(userDetails.getUser(), key);
    }
}


