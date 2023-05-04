package com.sparta.village.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MainResponseDto {
    private List<AcceptReservationResponseDto> dealList;
    private List<ProductResponseDto> randomProduct;
    private int zzimCount;
    private List<ProductResponseDto> latestProduct;
    private Integer visitorCount;

}
