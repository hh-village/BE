package com.sparta.village.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.product.entity.Product;

import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
        doAnswer(invocation -> {
            PutObjectRequest request = invocation.getArgument(0);
            String bucketname = request.getBucketName();
            String fileName = request.getKey();
            when(amazonS3.getUrl(bucketname, fileName)).thenReturn(new URL("https://" + bucketname + ".s3.amazonaws.com/" + fileName));
            return null;
        }).when(amazonS3).putObject(any(PutObjectRequest.class));

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
    @DisplayName("이미지 업로드-ImageListIsNull")
    public void testStoreFilesImageListIsNull() {
        List<MultipartFile> imageList = null;

        CustomException exception = assertThrows(CustomException.class, () -> {
            imageStorageService.storeFiles(imageList);
        });

        assertEquals(ErrorCode.IMAGE_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("storeFiles()-RuntimeException")
    public void testStoreFilesThrowException() throws IOException {
        // given
        MultipartFile image = mock(MultipartFile.class);
        when(image.getOriginalFilename()).thenReturn("testImage.jpg");
        when(image.getSize()).thenReturn(1L);
        when(image.getInputStream()).thenThrow(IOException.class);

        List<MultipartFile> imageList = List.of(image);

        // when
        Executable executable = () -> imageStorageService.storeFiles(imageList);

        // then
        RuntimeException exception = assertThrows(RuntimeException.class, executable);
        String actualMessage = exception.getMessage();
        String expectedMessage = "이미지 업로드 실패: ";

        assertTrue(actualMessage.startsWith(expectedMessage));
        assertTrue(actualMessage.contains("testImage.jpg"));
    }

    @Test
    @DisplayName("이미지리스트 저장")
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
    @DisplayName("이미지 삭제")
    public void testDeleteFile() {
        // given
        String fileUrl = "https://testBucket.s3.amazonaws.com/testFile.txt";
        String expectedFileName = "testFile.txt";
        String decodedFileName = URLDecoder.decode(fileUrl.substring(fileUrl.lastIndexOf("/") + 1), StandardCharsets.UTF_8);

        doAnswer(invocation -> {
            invocation.getArgument(0);
            return null;
        }).when(amazonS3).deleteObject(any(DeleteObjectRequest.class));

        // when
        imageStorageService.deleteFile(fileUrl);

        // then
        assertEquals(expectedFileName, decodedFileName);

        // verify
        verify(amazonS3, times(1)).deleteObject(any(DeleteObjectRequest.class)); // 수정된 코드
    }

    @Test
    @DisplayName("ProductId에 알맞는 이미지 삭제")
    public void testDeleteImageListByProductId() {
        // given
        Long productId = 1L;
        Image image1 = new Image(new Product(), "imageUrl1");
        Image image2 = new Image(new Product(), "imageUrl2");
        List<Image> imageList = Arrays.asList(image1, image2);
        when(imageRepository.findByProductId(productId)).thenReturn(imageList);
        doNothing().when(imageRepository).deleteById(image1.getId());
        doNothing().when(imageRepository).deleteById(image2.getId());

        // when
        imageStorageService.deleteImagesByProductId(productId);

        // then
        verify(imageRepository, times(1)).findByProductId(productId);
        verify(imageRepository, times(2)).deleteById(null);
    }


    @Test
    @DisplayName("ProductId에 알맞는 이미지 리스트 가져오기")
    public void testGetImageUrlListByProductId() {
        // given
        Long productId = 1L;

        List<Image> imageUrlList = Arrays.asList(
                new Image(new Product(), "https://example.com/image1.jpg"),
                new Image(new Product(), "https://example.com/image2.jpg")
        );
        when(imageRepository.findByProductId(productId)).thenReturn(imageUrlList);

        // when
        List<String> response = imageStorageService.getImageUrlListByProductId(productId);

        // then
        assertEquals(2, response.size());
        assertEquals("https://example.com/image1.jpg", response.get(0));
        assertEquals("https://example.com/image2.jpg", response.get(1));

        // verify
        verify(imageRepository, times(1)).findByProductId(productId);
    }

    @Test
    @DisplayName("ProductId에 알맞는 이미지리스트 중 첫번째 배열 가져오기")
    public void testGetFirstImageUrlByProductId() {
        // given
        Long productId = 1L;
        List<Image> imageUrlList = Arrays.asList(
                new Image(new Product(), "https://example.com/image1.jpg"),
                new Image(new Product(), "https://example.com/image2.jpg")
        );
        when(imageRepository.findByProductId(productId)).thenReturn(imageUrlList);

        // when
        String firstImageUrl = imageStorageService.getFirstImageUrlByProductId(productId);

        // then
        assertEquals("https://example.com/image1.jpg", firstImageUrl);

        // verify
        verify(imageRepository, times(1)).findByProductId(productId);
    }

}