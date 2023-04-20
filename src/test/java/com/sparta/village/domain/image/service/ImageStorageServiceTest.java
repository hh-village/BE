package com.sparta.village.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.product.entity.Product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class ImageStorageServiceTest {
    @InjectMocks
    private ImageStorageService imageStorageService;
    @Mock
    private AmazonS3 amazonS3;
    @Mock
    private ImageRepository imageRepository;

    @Test
    @DisplayName("이미지 업로드-정상")
    public void testStoreFiles() {
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        List<MultipartFile> fileList = List.of(file1, file2);
        String originalFilename1 = "test1.jpg";
        String originalfilename2 = "test2.jpg";

        when(file1.getOriginalFilename()).thenReturn(originalFilename1);
        when(file2.getOriginalFilename()).thenReturn(originalfilename2);
        when(file1.getSize()).thenReturn(100L);
        when(file2.getSize()).thenReturn(200L);
        when(amazonS3.getUrl(anyString(), anyString())).thenAnswer(invocation -> {
            String bucketname = invocation.getArgument(0);
            String fileName = invocation.getArgument(1);
            return new URL("https://" + bucketname + "s3.amazonaws.com/" + fileName);
        });

        //when
        List<String> result = imageStorageService.storeFiles(fileList);

        //then
        assertEquals(2, result.size());
        assertTrue(result.get(0).contains(originalFilename1));
        assertTrue(result.get(1).contains(originalfilename2));

        //verify
        verify(amazonS3, times(2)).putObject(any(PutObjectRequest.class));
    }

    @Test
    public void testSaveImageList() {
        //given
        Product product = new Product();
        List<String> imageUrlList = List.of("testUrl1","testUrl2");

        //when
        imageStorageService.saveImageList(product, imageUrlList);

        //verify
        verify(imageRepository, times(2)).saveAndFlush(any(Image.class));
    }

    @Test
    public void testDeleteFile() {
        //given
        String fileUrl = "https://examplebucket.s3.amazonaws.com/UUID_test.jpg";
        String fileName = "UUID_test.jpg";
        String bucketName = "examplebucket";

        //when
        imageStorageService.deleteFile(fileUrl);

        //verify
        verify(amazonS3).deleteObject(new DeleteObjectRequest(bucketName, fileName));
    }

    @Test
    public void testDeleteImagesByProductId() {
        //given
        Long productId = 1L;
        List<Image> imageList = new ArrayList<>();
        Image image1 = new Image(new Product(), "imageUrl1");
        Image image2 = new Image(new Product(), "imageUrl2");
        imageList.add(image1);
        imageList.add(image2);

        when(imageRepository.findByProductId(productId)).thenReturn(imageList);

        //when
        imageStorageService.deleteImagesByProductId(productId);

        //verify
        verify(imageRepository).findByProductId(productId);
        verify(imageStorageService, times(2)).deleteFile(anyString());
        verify(imageStorageService, times(2)).deleteImagesByProductId(anyLong());
    }

//    @Transactional
//    public List<String> storeFiles(List<MultipartFile> imageList) {
//        if (imageList == null || imageList.isEmpty()) {
//            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
//        }
//        List<String> imageUrlList = new ArrayList<>();
//        for (MultipartFile image : imageList) {
//            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
//            try {
//                ObjectMetadata metadata = new ObjectMetadata();
//                metadata.setContentLength(image.getSize());
//                amazonS3.putObject(new PutObjectRequest(bucketName, fileName, image.getInputStream(), metadata));
//            } catch (IOException e) {
//                throw new RuntimeException("이미지 업로드 실패: " + fileName, e);
//            }
//            //S3 버킷 내에 저장된 파일의 URL 생성
//            String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();
//            imageUrlList.add(fileUrl);
//        }
//        return imageUrlList;
//    }
//
//    public void saveImageList(Product product, List<String> imageUrlList) {
//        for (String imageUrl : imageUrlList) {
//            imageRepository.saveAndFlush(new Image(product, imageUrl));
//        }
//    }
//
//    public void deleteFile(String fileUrl) {
//        String fileName = URLDecoder.decode(fileUrl.substring(fileUrl.lastIndexOf("/") + 1), StandardCharsets.UTF_8);
//        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
//    }
//
//    public void deleteImagesByProductId(Long productId) {
//        List<Image> imageList = imageRepository.findByProductId(productId);
//        for (Image image : imageList) {
//            deleteFile(image.getImageUrl());
//            imageRepository.deleteById(image.getId());
//        }
//    }
//
//    public List<String> getImageUrlListByProductId(Long id) {
//        return imageRepository.findByProductId(id).stream().map(Image::getImageUrl).toList();
//    }
//
//    public String getFirstImageUrlByProductId(Long id) {
//        return getImageUrlListByProductId(id).stream()
//                .findFirst()
//                .orElse(null);
//    }

}