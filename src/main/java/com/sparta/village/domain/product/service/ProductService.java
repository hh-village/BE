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



@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    public ResponseEntity<ResponseMessage> registProduct(User user, ProductRequestDto productRequestDto) {
        productRepository.saveAndFlush(new Product(user, productRequestDto));
        return ResponseMessage.SuccessResponse("성공적으로 제품 등록이 되었습니다.", "");
    }
    public ResponseEntity<ResponseMessage> deleteProductById(Long id,User user) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );
        if(!product.getUserId().equals(user.getId())) {
            throw new CustomException(ErrorCode.DELETE_NOT_FOUND);
        }
            productRepository.deleteById(id);
            return ResponseMessage.SuccessResponse("삭제 성공했습니다.","");
    }

}
