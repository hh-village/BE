package com.sparta.village.domain.user.dto;

import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyProductsResponseDto {
    private Long id;
    private String title;
    private String image;
    private String createdAt;
}
