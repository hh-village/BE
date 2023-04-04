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
    private double latitude;    //위도
    private double longitude;   //경도
    private List<MultipartFile> images;
}
