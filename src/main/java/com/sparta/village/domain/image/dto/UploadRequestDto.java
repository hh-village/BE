package com.sparta.village.domain.image.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UploadRequestDto {
    private MultipartFile image;
}
