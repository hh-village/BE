package com.sparta.village.domain.image.controller;

import com.sparta.village.domain.image.dto.UploadRequestDto;
import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.image.service.FileStorageService;
import com.sparta.village.global.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping(value = "/products/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseMessage> uploadFile(@ModelAttribute UploadRequestDto image) {
        System.out.println("=========클릭되니?=========");
        return fileStorageService.storeFile(image.getImage());
    }
}