package com.sparta.village.domain.product.dto;

import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.reservation.dto.ReservationResponseDto;
import com.sparta.village.domain.reservation.entity.Reservation;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.List;

@Getter
public class ProductDetailResponseDto {

    private final Long id;
    private final String title;
    private final String description;
    private final int price;
    private final List<String> imageList;
    private final String profile;
    private final String ownerNickname;
    private final String location;
    private final boolean checkOwner;
    private final List<ReservationResponseDto> reservationList;

    public ProductDetailResponseDto(Product product, boolean checkOwner, List<String> imageUrlList, String ownerNickname, String ownerProfile, List<ReservationResponseDto> reservationList) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.imageList = imageUrlList;
        this.profile = ownerProfile;
        this.location = product.getLocation();
        this.ownerNickname = ownerNickname;
        this.checkOwner = checkOwner;
        this.reservationList = reservationList;
    }
}
