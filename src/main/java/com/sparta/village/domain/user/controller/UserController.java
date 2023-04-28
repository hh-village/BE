package com.sparta.village.domain.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.user.dto.NicknameRequestDto;
import com.sparta.village.domain.user.service.KakaoUserService;
import com.sparta.village.domain.user.service.UserService;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
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
        return kakaoUserService.kakaoLogin(code, response);
    }

    @PatchMapping("/users")
    public ResponseEntity<ResponseMessage> updateNickname(@RequestBody NicknameRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.updateNickname(requestDto.getNickname(), userDetails.getUser());
    }

    @GetMapping("/users")
    public ResponseEntity<ResponseMessage> getUsersItems(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam(required = false) String key) {
        return userService.getUserItemList(userDetails.getUser(), key);
    }

//    @PostMapping("/test/login/{nickname}")
//    public ResponseEntity<ResponseMessage> testLogin(@PathVariable String nickname, HttpServletResponse response){
//        return kakaoUserService.testLogin(nickname, response);
//    }
}


