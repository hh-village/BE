package com.sparta.village.domain.reservation.controller;

import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.reservation.dto.RentResponseDto;
import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import com.sparta.village.domain.reservation.dto.ReservationResponseDto;
import com.sparta.village.domain.reservation.dto.StatusRequestDto;
import com.sparta.village.domain.reservation.service.ReservationService;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping("/products/{id}/reserve")
    public ResponseEntity<ResponseMessage> reserve(@PathVariable Long id, @RequestBody ReservationRequestDto requestDto,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (requestDto.getStartDate().isAfter(requestDto.getEndDate())) {
            throw new CustomException(ErrorCode.NOT_PROPER_DATE);
        }
        return reservationService.reserve(id, requestDto, userDetails.getUser());
    }

    @GetMapping("/users/rents")
    public List<RentResponseDto> getRentedItems(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reservationService.getRentedItems(userDetails.getUser());
    }

    @DeleteMapping("/products/reservation/{id}")
    public ResponseEntity<ResponseMessage> deleteReservation(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reservationService.deleteReservation(id, userDetails.getUser());
    }
    //



    @PatchMapping("/products/reservation/{id}/status")
    public ResponseEntity<ResponseMessage> changeStatus(@PathVariable Long id, @RequestBody StatusRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return reservationService.changeStatus(id, requestDto, userDetails.getUser().getId());
    }

    @GetMapping("/products/reservations")
    public ResponseEntity<ResponseMessage> getAcceptedReservationList() {
        return reservationService.getAcceptedReservationList();
    }
}
