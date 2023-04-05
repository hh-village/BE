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

    private Long id;
    private String title;
    private String description;
    private int price;
    private List<String> image;
    private String profile;
    private String ownerNickname;
    private double latitude;
    private double longitude;
    private boolean isOwner;
    private List<ReservationResponseDto> reservationList;

    public ProductDetailResponseDto(Product product, boolean isOwner, List<String> imageUrls, String ownerNickname, List<ReservationResponseDto> reservationList) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.image = imageUrls;
        this.latitude = product.getLatitude();
        this.longitude = product.getLongitude();
        this.ownerNickname = ownerNickname;
        this.isOwner = isOwner;
        this.reservationList = reservationList;
    }
}
