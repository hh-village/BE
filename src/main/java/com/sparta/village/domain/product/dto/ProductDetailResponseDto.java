package com.sparta.village.domain.product.dto;


import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.reservation.dto.ReservationResponseDto;
import lombok.Getter;



import java.util.List;

@Getter
public class ProductDetailResponseDto {

    private final Long id;
    private final String profile;
    private final String ownerNickname;
    private final String title;
    private final String description;
    private final int price;
    private final String location;
    private final boolean zzimStatus;
    private final int zzimCount;
    private final boolean checkOwner;
    private final List<String> imageList;
    private final List<ReservationResponseDto> reservationList;


    public ProductDetailResponseDto(Product product, boolean checkOwner,boolean zzimStatus, int zzimCount, List<String> imageUrlList, String ownerNickname, String ownerProfile, List<ReservationResponseDto> reservationList) {
        this.id = product.getId();
        this.profile = ownerProfile;
        this.ownerNickname = ownerNickname;
        this.title = product.getTitle();
        this.description = product.getDescription();
        this.price = product.getPrice();
        this.location = product.getLocation();
        this.zzimStatus = zzimStatus;
        this.checkOwner = checkOwner;
        this.zzimCount = zzimCount;
        this.imageList = imageUrlList;
        this.reservationList = reservationList;
    }
}
