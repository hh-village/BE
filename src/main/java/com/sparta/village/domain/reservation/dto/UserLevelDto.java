package com.sparta.village.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLevelDto {
    private LocalDate startDate;
    private LocalDate endDate;
}
