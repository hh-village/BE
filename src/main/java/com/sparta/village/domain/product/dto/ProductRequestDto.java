package com.sparta.village.domain.product.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequestDto {
    private String title;
    private String description;
    private int price;
    private String location;
    private List<MultipartFile> images;
}
