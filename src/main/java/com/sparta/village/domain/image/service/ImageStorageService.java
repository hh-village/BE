package com.sparta.village.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageStorageService {
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;
    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;

    @Transactional
    public List<String> storeFiles(List<MultipartFile> imageList) {
        if (imageList == null || imageList.isEmpty()) {
            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
        }
        List<String> imageUrlList = new ArrayList<>();

        imageList.stream().forEach(image -> {
            if (image.getSize() > 20 * 1024 * 1024) {
                throw new RuntimeException("파일 용량 초과: " + image.getOriginalFilename());
            }
        });
        for (MultipartFile image : imageList) {
            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
            try {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(image.getSize());
                amazonS3.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), metadata));
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패: " + fileName, e);
            }
            //S3 버킷 내에 저장된 파일의 URL 생성
            String fileUrl = "https://" + cloudFrontDomain + "/" + fileName;
            imageUrlList.add(fileUrl);
        }
        return imageUrlList;
    }

    public void saveImageList(Product product, List<String> imageUrlList) {
        for (String imageUrl : imageUrlList) {
            imageRepository.saveAndFlush(new Image(product, imageUrl));
        }
    }

    public void deleteFile(String fileUrl) {
        String fileName = URLDecoder.decode(fileUrl.substring(fileUrl.lastIndexOf("/") + 1), StandardCharsets.UTF_8);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    public void deleteImagesByProductId(Long productId) {
        List<Image> imageList = imageRepository.findByProductId(productId);
        for (Image image : imageList) {
            deleteFile(image.getImageUrl());
            imageRepository.deleteById(image.getId());
        }
    }

    public List<String> getImageUrlListByProductId(Long id) {
        return imageRepository.findByProductId(id).stream().map(Image::getImageUrl).toList();
    }

    public String getFirstImageUrlByProductId(Long id) {
        return getImageUrlListByProductId(id).stream()
                .findFirst()
                .orElse(null);
    }
}
