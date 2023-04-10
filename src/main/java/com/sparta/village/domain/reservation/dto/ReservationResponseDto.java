package com.sparta.village.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ReservationResponseDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;

    private Long productId;

    private String status;
    private String nickname;
}
