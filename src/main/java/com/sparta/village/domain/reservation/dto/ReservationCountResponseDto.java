package com.sparta.village.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCountResponseDto {
    private Long productId;
    private Long reservationCount;
}
