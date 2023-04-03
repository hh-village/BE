package com.sparta.village.domain.reservation.controller;

import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import com.sparta.village.domain.reservation.service.ReservationService;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("/products/{id}/reserve")
    public ResponseEntity<ResponseMessage> reserve(@PathVariable Long id, @RequestBody ReservationRequestDto requestDto,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reservationService.reserve(id, requestDto, userDetails.getUser().getId());
    }
}
