package com.sparta.village.domain.image.controller;

import com.sparta.village.domain.image.service.ImageStorageService;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageStorageService imageStorageService;

    @PostMapping(value = "/products/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<ResponseMessage> uploadFile(@RequestParam List<MultipartFile> images) {
        return imageStorageService.storeFiles(images);
    }
}