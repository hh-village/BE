package com.sparta.village.domain.product.dto;

import com.sparta.village.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDto {
    private List<ProductResponseDto> productList;
    private boolean checkLast;
}
