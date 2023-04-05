package com.sparta.village.domain.product.service;

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
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ImageStorageService imageStorageService;
    private final ReservationService reservationService;
    private final KakaoUserService kakaoUserService;

    @Transactional
    public ResponseEntity<ResponseMessage> registProduct(User user, ProductRequestDto productRequestDto) {
        // 이미지를 S3에 업로드하고 파일 URL 목록을 가져옴
        List<String> fileUrlList = imageStorageService.storeFiles(productRequestDto.getImages());
        // 새로운 Product 객체를 생성하고 저장합니다.
        Product newProduct = new Product(user, productRequestDto);
        productRepository.saveAndFlush(newProduct);
        // 이미지 URL을 이용하여 이미지 엔티티를 생성하고 저장합니다.
        imageStorageService.saveImageList(newProduct, fileUrlList);
        return ResponseMessage.SuccessResponse("성공적으로 제품 등록이 되었습니다.", "");
    }

    @Transactional
    public ResponseEntity<ResponseMessage> deleteProductById(Long id, User user) {
        // 데이터베이스에서 지정된 ID의 제품을 검색
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        // 사용자가 제품을 삭제할 권한이 있는지 확인
        if (!(Objects.equals(product.getUserId(), user.getId()))) {
            throw new CustomException(ErrorCode.DELETE_NOT_FOUND);
        }
        // 데이터베이스에서 제품과 연결된 이미지를 검색
        imageStorageService.deleteImagesByProductId(id);
        // 데이터베이스에서 제품을 삭제
        productRepository.deleteById(id);
        // 성공적인 응답을 반환
        return ResponseMessage.SuccessResponse("상품 삭제가 되었습니다.","");
    }

    @Transactional
    public ResponseEntity<ResponseMessage> detailProduct(UserDetailsImpl userDetails, Long id) {
        boolean checkOwner = false;
        if (userDetails != null) {
            checkOwner = checkProductOwner(id, userDetails.getUser().getId());
        }
        Product product = findProductById(id);
        List<ReservationResponseDto> reservationList = reservationService.getReservationList();
        List<String> imageList = imageStorageService.getImageUrlsByProductId(id);
        User owner = kakaoUserService.getUserByUserId(Long.toString(product.getUserId()));
        String ownerNickname = owner.getNickname();
        String ownerProfile = owner.getProfile();

        ProductDetailResponseDto productDetailResponseDto = new ProductDetailResponseDto(product, checkOwner, imageList, ownerNickname, ownerProfile,reservationList);

        return ResponseMessage.SuccessResponse("제품 조회가 완료되었습니다.", productDetailResponseDto);
    }

    //로그인한 유저가 제품 등록자가 맞는지 체크. 제품을 등록한 판매자이면 true 반환.
    private boolean checkProductOwner(Long productId, Long userId) {
        return productRepository.existsByIdAndUserId(productId, userId);
    }

    @Transactional(readOnly = true)
    public Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}
