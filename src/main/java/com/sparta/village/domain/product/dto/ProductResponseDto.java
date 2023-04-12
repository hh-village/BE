package com.sparta.village.domain.product.dto;

import com.sparta.village.domain.product.entity.Product;
import lombok.Getter;

@Getter
public class ProductResponseDto {
    private Product product;
    private String primeImageUrl;

    public ProductResponseDto(Product product, String primeImageUrl) {
        this.product = product;
        this.primeImageUrl = primeImageUrl;
    }
}
