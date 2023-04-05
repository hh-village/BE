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
    private final List<String> image;
    private final String profile;
    private final String ownerNickname;
    private final double latitude;
    private final double longitude;
    private final boolean checkOwner;
    private final List<ReservationResponseDto> reservationList;

    public ProductDetailResponseDto(Product product, boolean isOwner, List<String> imageUrls, String ownerNickname, String ownerProfile, List<ReservationResponseDto> reservationList) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.image = imageUrls;
        this.profile = ownerProfile;
        this.latitude = product.getLatitude();
        this.longitude = product.getLongitude();
        this.ownerNickname = ownerNickname;
        this.checkOwner = isOwner;
        this.reservationList = reservationList;
    }
}
