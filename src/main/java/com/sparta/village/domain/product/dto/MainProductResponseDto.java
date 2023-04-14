package com.sparta.village.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MainProductResponseDto {
    private Long id;
    private String image;
    private String title;
    private String location;
    private int price;
    private boolean hot;
    private boolean checkZzim;
}
