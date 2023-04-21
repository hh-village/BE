package com.sparta.village.domain.product.service;

import com.sparta.village.domain.chat.service.ChatService;
import com.sparta.village.domain.image.service.ImageStorageService;
import com.sparta.village.domain.product.dto.*;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.dto.AcceptReservationResponseDto;
import com.sparta.village.domain.reservation.dto.ReservationCountResponseDto;
import com.sparta.village.domain.reservation.service.ReservationService;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.service.UserService;
import com.sparta.village.domain.zzim.service.ZzimService;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final UserService userService;
    private final ZzimService zzimService;
    private final ChatService chatRoomService;
    private final ReservationService reservationService;
    private final ImageStorageService imageStorageService;


    @Transactional
    public ResponseEntity<ResponseMessage> getMainPage(UserDetailsImpl userDetails) {
        User user = userDetails == null ? null : userDetails.getUser();
        List<AcceptReservationResponseDto> dealList = reservationService.getAcceptedReservationList();
        List<ProductResponseDto> productList = productRepository.findRandomProduct(8).stream()
                .map(p -> new ProductResponseDto(p, searchPrimeImageUrl(p), isMostProduct(p), zzimService.getZzimStatus(user, p))).toList();
        List<ProductResponseDto> randomProduct = productRepository.findRandomProduct(6).stream()
                .map(p -> new ProductResponseDto(p, searchPrimeImageUrl(p), isMostProduct(p), zzimService.getZzimStatus(user, p))).toList();
        return ResponseMessage.SuccessResponse("메인페이지 조회되었습니다.", new MainResponseDto(dealList, productList, zzimService.getZzimCount(user), randomProduct));
    }

    @Transactional
    public ResponseEntity<ResponseMessage> registProduct(User user, ProductRequestDto productRequestDto) {
        Product newProduct = new Product(user, productRequestDto);
        productRepository.saveAndFlush(newProduct);
        imageStorageService.saveImageList(newProduct, imageStorageService.storeFiles(productRequestDto.getImages()));

        return ResponseMessage.SuccessResponse("성공적으로 제품 등록이 되었습니다.", "");
    }

    @Transactional
    public ResponseEntity<ResponseMessage> deleteProduct(Long id, User user) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        if (!product.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.NOT_AUTHOR);
        }


        chatRoomService.deleteMessagesByProductId(id);
        chatRoomService.deleteByProductId(id);
        reservationService.deleteByProductId(id);
        zzimService.deleteByProductId(id);
        imageStorageService.deleteImagesByProductId(id);
        productRepository.deleteById(id);

        return ResponseMessage.SuccessResponse("상품 삭제가 되었습니다.", "");
    }

    @Transactional
    public ResponseEntity<ResponseMessage> updateProduct(Long id, User user, ProductRequestDto productRequestDto) {

        Product product = findProductById(id);
        if (!product.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.NOT_AUTHOR);
        }

        List<MultipartFile> images = productRequestDto.getImages();
        if (images != null && !images.isEmpty()) {
            imageStorageService.deleteImagesByProductId(id);
            imageStorageService.saveImageList(product, imageStorageService.storeFiles(images));
        }

        product.update(productRequestDto);
        return ResponseMessage.SuccessResponse("상품 수정이 되었습니다.", "");
    }

    @Transactional
    public ResponseEntity<ResponseMessage> detailProduct(UserDetailsImpl userDetails, Long id) {
        Product product = findProductById(id);
        User owner = userService.getUserByUserId(Long.toString(product.getUser().getId()));
        User user = userDetails == null ? null : userDetails.getUser();
        boolean checkOwner = user != null && checkProductOwner(id, user.getId());
        boolean zzimStatus = user != null && zzimService.getZzimStatus(user, product);

        ProductDetailResponseDto productDetailResponseDto = new ProductDetailResponseDto(product, checkOwner, zzimStatus,
                zzimService.countByProductId(id), imageStorageService.getImageUrlListByProductId(id),
                owner.getNickname(), owner.getProfile(), reservationService.getReservationList(user, id));

        return ResponseMessage.SuccessResponse("제품 조회가 완료되었습니다.", productDetailResponseDto);
    }

    public ResponseEntity<ResponseMessage> searchProductList(UserDetailsImpl userDetails, String qr, String location) {
        User user = userDetails == null ? null : userDetails.getUser();
        List<Product> productList;

        if (qr == null && location == null) {
            productList = productRepository.findAll();
        } else if (qr == null) {
            productList = productRepository.findByLocationContaining(location);
        } else if (location == null) {
            productList = productRepository.findByTitleContaining(qr);
        } else {
            productList = productRepository.findByTitleContainingAndLocationContaining(qr, location);
        }

        List<ProductResponseDto> responseList = productList.stream()
                .map(product -> new ProductResponseDto(product, searchPrimeImageUrl(product), isMostProduct(product), zzimService.getZzimStatus(user, product)))
                .toList();

        return ResponseMessage.SuccessResponse("검색 조회가 되었습니다.", responseList);
    }

    @Transactional(readOnly = true)
    public Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public String searchPrimeImageUrl(Product product) {
        return imageStorageService.getImageUrlListByProductId(product.getId()).get(0);
    }

    //로그인한 유저가 제품 등록자가 맞는지 체크. 제품을 등록한 판매자이면 true 반환.
    public boolean checkProductOwner(Long productId, Long userId) {
        return productRepository.existsByIdAndUserId(productId, userId);
    }

    public boolean isMostProduct(Product product) {
        List<ReservationCountResponseDto> reservationCountList = reservationService.reservationCount();

        int index = (int) Math.ceil(reservationCountList.size() * 0.1) - 1;
        if (index < 0) return false;

        for (ReservationCountResponseDto responseDto : reservationCountList) {
            if (responseDto.getReservationCount() >= Math.toIntExact(reservationCountList.get(index).getReservationCount())) {
                Optional<Product> isMostProduct = productRepository.findById(responseDto.getProductId());
                if (isMostProduct.isPresent() && isMostProduct.get().getId().equals(product.getId())) {
                    return true;
                }
            }
        }
        return false;
    }
}