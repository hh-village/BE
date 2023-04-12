package com.sparta.village.domain.zzim.controller;

import com.sparta.village.domain.zzim.service.ZzimService;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ZzimController {

    private final ZzimService zzimService;

    @PostMapping("/products/{id}/zzim")
    public ResponseEntity<ResponseMessage> zzim(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return zzimService.zzim(id, userDetails.getUser());
    }
}
