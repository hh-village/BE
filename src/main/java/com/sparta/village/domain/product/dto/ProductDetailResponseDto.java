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
    private final int ownerReturned;
    private final int ownerAccepted;
    private final int ownerWaiting;
    private final String title;
    private final String description;
    private final int price;
    private final String location;
    private final boolean zzimStatus;
    private final int zzimCount;
    private final boolean checkOwner;
    private final List<String> imageList;
    private final List<ReservationResponseDto> reservationList;


    public ProductDetailResponseDto(Long productId, String title, String description, int price, String location, boolean checkOwner,String ownerNickname,String ownerProfile,int zzimCount,int ownerReturned, int ownerAccepted, int ownerWaiting, boolean zzimStatus, List<String> imageUrlList, List<ReservationResponseDto> reservationList) {
        this.id = productId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.profile = ownerProfile;
        this.ownerNickname = ownerNickname;
        this.ownerReturned = ownerReturned;
        this.ownerAccepted = ownerAccepted;
        this.ownerWaiting = ownerWaiting;
        this.zzimStatus = zzimStatus;
        this.checkOwner = checkOwner;
        this.zzimCount = zzimCount;
        this.imageList = imageUrlList;
        this.reservationList = reservationList;
    }
}
