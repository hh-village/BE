package com.sparta.village.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String nickname;

}
