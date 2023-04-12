package com.sparta.village.domain.product.dto;


import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



import java.time.format.DateTimeFormatter;


@Getter
@Setter
@NoArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String title;
    private String createdAt;
    private String ImageUrl;



    public ProductResponseDto(Product product, Image image) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.id = product.getId();
        this.title = product.getTitle();
       // this.createdAt = product.getCreatedAt().format(formatter);
        this.ImageUrl = image.getImageUrl();
        if(product.getCreatedAt() == null){
            this.createdAt = null;
        }else{
            this.createdAt = product.getCreatedAt().format(formatter);
        }
    }
}