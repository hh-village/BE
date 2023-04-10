package com.sparta.village.domain.follow.controller;

import com.sparta.village.domain.follow.service.FollowService;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

//    @PostMapping("/users/{id}/follow")
//    public ResponseEntity<ResponseMessage> follow(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        return followService.follow(id, userDetails.getUser());
//    }
}
