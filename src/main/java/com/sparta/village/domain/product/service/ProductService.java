package com.sparta.village.domain.product.service;

import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.image.service.ImageStorageService;
import com.sparta.village.domain.product.dto.ProductDetailResponseDto;
import com.sparta.village.domain.product.dto.ProductRequestDto;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.dto.ReservationResponseDto;
import com.sparta.village.domain.reservation.service.ReservationService;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.service.KakaoUserService;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ImageStorageService imageStorageService;
    private final ImageRepository imageRepository;
    private final ReservationService reservationService;
    private final KakaoUserService kakaoUserService;
    @Transactional
    public ResponseEntity<ResponseMessage> registProduct(User user, ProductRequestDto productRequestDto) {
        // 이미지를 S3에 업로드하고 파일 URL 목록을 가져옴
        ResponseEntity<ResponseMessage> response = imageStorageService.storeFiles(productRequestDto.getImages());
        List<String> fileUrls = (List<String>) response.getBody().getData();

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


    //로그인한 유저가 제품 등록자가 맞는지 체크. 제품을 등록한 판매자이면 true 반환.
    private boolean checkProductOwner(Long productId, Long userId) {
        return productRepository.existsByIdAndUserId(productId, userId);
    }
    @Transactional
    public ResponseEntity<ResponseMessage> detailProduct(User user, Long id) {
        Product product = findProductById(id);
        boolean isOwner = checkProductOwner(id, user.getId());
        List<ReservationResponseDto> reservationList = reservationService.getReservationList();
        List<String> imageList = imageStorageService.getImageUrlsByProductId(id);
        User owner = kakaoUserService.getUserByUserId(Long.toString(product.getUserId()));
        String ownerNickname = owner.getNickname();
        String ownerProfile = owner.getProfile();

        ProductDetailResponseDto productDetailResponseDto = new ProductDetailResponseDto(product, isOwner, imageList, ownerNickname, ownerProfile,reservationList);

        return ResponseMessage.SuccessResponse("제품 조회가 완료되었습니다.", productDetailResponseDto);
    }

    @Transactional(readOnly = true)
    public Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
