package com.sparta.village.domain.product.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    private String title;
    private String description;
    private int price;
    private String location;
    private List<MultipartFile> images;

}
