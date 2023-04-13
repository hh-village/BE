package com.sparta.village.domain.user.dto;

import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.user.entity.User;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class MyProductsResponseDto {
    private final Long id;
    private final String title;
    private final String image;
    private final String createdAt;

    public MyProductsResponseDto(User user, Product product, ImageRepository imageRepository) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.id = product.getId();
        this.title = product.getTitle();
        this.image = imageRepository.findFirstByProductId(product.getId())
                                       .map(Image::getImageUrl)
                                       .orElse(null);
        if (product.getCreatedAt() == null) {
            this.createdAt = null;
        } else {
            this.createdAt = product.getCreatedAt().format(formatter);
        }
    }
}