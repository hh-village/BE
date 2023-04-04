package com.sparta.village.domain.product.service;

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

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    public ResponseEntity<ResponseMessage> registProduct(User user, ProductRequestDto productRequestDto) {
        productRepository.saveAndFlush(new Product(user, productRequestDto));
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
