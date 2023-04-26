package com.sparta.village.domain.product.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    @NotNull(message = "제목을 입력해주세요.")
    private String title;
    @NotNull(message = "제품설명을 입력해 주세요.")
    private String description;
    @NotNull(message = "가격을 입력해 주세요.")
    private int price;
    @NotNull(message = "거래지역을 입력해 주세요.")
    private String location;
    @NotNull(message = "제품이미지를 첨부해 주세요.")
    private List<MultipartFile> images;

}
