package com.sparta.village.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.global.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageStorageService {
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    private final ImageRepository imageRepository;

    public ResponseEntity<ResponseMessage> storeFiles(List<MultipartFile> files) {
        List<String> fileUrlList = new ArrayList<>();
        for(MultipartFile file : files) {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            try {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.getSize());
                amazonS3.putObject(new PutObjectRequest(bucketName, fileName, file.getInputStream(), metadata));
            } catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패: " + fileName, e);
            }
            //S3 버킷 내에 저장된 파일의 URL 생성
            String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
            fileUrlList.add(fileUrl);
        }
        return ResponseMessage.SuccessResponse("성공적으로 업로드 되었습니다.", fileUrlList);
    }

    public String getFileUrl(String fileName) {
        return amazonS3.getUrl(bucketName, fileName).toString();
    }

    public void deleteFile(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    public List<String> getImageUrlsByProductId(Long id) {
        return imageRepository.findByProductId(id).stream().map(Image::getImageUrl).toList();
//        List<Image> imageList = imageRepository.findByProductId(id);
//        List<String> imageUrlList = new ArrayList<>();
//        for(Image image : imageList) {
//            imageUrlList.add(image.getImageUrl());
//        }
//        return imageUrlList;
    }
}
