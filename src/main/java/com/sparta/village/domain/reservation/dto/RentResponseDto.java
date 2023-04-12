package com.sparta.village.domain.reservation.dto;


import com.sparta.village.domain.reservation.entity.Reservation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
public class RentResponseDto {
    private Long id;
    private String title;
    private String imageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;



    public RentResponseDto(Reservation reservation,String imageUrl) {
        this.id = reservation.getId();
        this.title = reservation.getProduct().getTitle();
        this.imageUrl = imageUrl;
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.status = reservation.getStatus();
    }
}

