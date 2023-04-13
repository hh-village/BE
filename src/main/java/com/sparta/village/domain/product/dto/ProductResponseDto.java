package com.sparta.village.domain.product.dto;


import com.sparta.village.domain.product.entity.Product;
import lombok.Getter;

@Getter
public class ProductResponseDto {
    private final Long id;
    private final String title;
    private final String image;
    private final String location;
    private final int price;

    public ProductResponseDto(Product product, String primeImageUrl) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.location = product.getLocation();
        this.price = product.getPrice();
        this.image = primeImageUrl;
    }
}
