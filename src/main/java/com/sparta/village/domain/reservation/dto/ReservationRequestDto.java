package com.sparta.village.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern="yyyy-MM-dd")
    private LocalDate endDate;

}
