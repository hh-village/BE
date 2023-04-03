package com.sparta.village.domain.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequestDto {
    private String title;
    private String description;
    private int price;
    private String location;
    private String imageUrl;
}
