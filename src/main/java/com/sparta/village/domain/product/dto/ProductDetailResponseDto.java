package com.sparta.village.domain.product.dto;

import com.sparta.village.domain.image.entity.Image;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.List;

public class ProductDetailResponseDto {

    private Long id;
    private List<Image> image;
    private String profile;
    private String ownerNickname;
    private boolean isOwner;
}
