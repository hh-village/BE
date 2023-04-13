package com.sparta.village.domain.user.dto;

import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.user.entity.User;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MyReservationsResponseDto {

    private final Long id;
    private final String title;
    private final String image;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String status;

    public MyReservationsResponseDto(User user, Reservation reservation, ImageRepository imageRepository) {
        this.id = reservation.getId();
        this.title = reservation.getProduct().getTitle();
        this.image = imageRepository.findFirstByProductId(reservation.getProduct().getId())
                                       .map(Image::getImageUrl)
                                       .orElse(null);
        this.startDate = reservation.getStartDate();
        this.endDate = reservation.getEndDate();
        this.status = reservation.getStatus();
    }
}
