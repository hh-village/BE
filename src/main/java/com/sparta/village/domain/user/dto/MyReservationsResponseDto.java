package com.sparta.village.domain.user.dto;

import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyReservationsResponseDto {

    private Long id;
    private Long productId;
    private String title;
    private String image;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
