package com.sparta.village.domain.zzim.controller;

import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.entity.UserRoleEnum;
import com.sparta.village.domain.zzim.service.ZzimService;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZzimControllerTest {
    @InjectMocks
    private ZzimController zzimController;
    @Mock
    private ZzimService zzimService;
    @Test
    @DisplayName("찜하기")
    public void testZzim() {
        //given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, "123");

        ResponseMessage responseMessage = new ResponseMessage("찜하기 성공", 200, new Object());
        when(zzimService.zzim(1L, userDetails.getUser())).thenReturn(ResponseEntity.ok(responseMessage));

        // when
        ResponseEntity<ResponseMessage> result = zzimController.zzim(1L, userDetails);

        //then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("찜하기 성공", Objects.requireNonNull(result.getBody()).getMessage());
        verify(zzimService).zzim(1L, userDetails.getUser());
    }
}