package com.sparta.village.domain.product.service;

import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.image.service.ImageStorageService;
import com.sparta.village.domain.product.dto.ProductRequestDto;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ImageStorageService imageStorageService;
    private final ImageRepository imageRepository;
    @Transactional
    public ResponseEntity<ResponseMessage> registProduct(User user, ProductRequestDto productRequestDto) {
        // 이미지를 S3에 업로드하고 파일 URL 목록을 가져옴
        ResponseEntity<ResponseMessage> response = imageStorageService.storeFiles(productRequestDto.getImages());
        List<String> fileUrls = (List<String>) response.getBody().getData();
        System.out.println(fileUrls.toString());

        // 새로운 Product 객체를 생성하고 저장합니다.
        Product newProduct = new Product(user, productRequestDto);
        productRepository.saveAndFlush(newProduct);

        // 이미지 URL을 ProductImage 객체로 변환하고 저장합니다.
        for (String fileUrl : fileUrls) {

            Image image = new Image(newProduct, fileUrl);
            imageRepository.saveAndFlush(image);
        }
        productRepository.saveAndFlush(newProduct);

        return ResponseMessage.SuccessResponse("성공적으로 제품 등록이 되었습니다.", "");
    }

    @Transactional
    public void checkProductId(Long id) {
        if (!productRepository.existsById(id)) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
    }

    @Transactional
    public void checkProductOwner(Long productId, Long userId) {
        if (!productRepository.existsByIdAndUserId(productId, userId)) {
            throw new CustomException(ErrorCode.NOT_SELLER);
        }
    }

}
