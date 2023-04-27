package com.sparta.village.domain.product.dto;

import com.sparta.village.domain.reservation.dto.AcceptReservationResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MainResponseDto {
    private List<AcceptReservationResponseDto> dealList;
    private List<ProductResponseDto> productList;
    private int zzimCount;
    private List<ProductResponseDto> randomProduct;
    private Long visitorCount;

}
