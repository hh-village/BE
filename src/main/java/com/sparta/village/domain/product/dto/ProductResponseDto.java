package com.sparta.village.domain.product.dto;


import com.sparta.village.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDto {
    private Long id;
    private String title;
    private String image;
    private String location;
    private int price;
    private boolean hot;
    private boolean checkZzim;

    public ProductResponseDto(Product product, String primeImageUrl, boolean hot, boolean checkZzim) {
        this.id = product.getId();
        this.title = product.getTitle();
        this.location = product.getLocation();
        this.price = product.getPrice();
        this.image = primeImageUrl;
        this.hot = hot;
        this.checkZzim = checkZzim;
    }
}
