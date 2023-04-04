package com.sparta.village.domain.product.controller;

import com.sparta.village.domain.product.dto.ProductRequestDto;
import com.sparta.village.domain.product.service.ProductService;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<ResponseMessage> registProduct(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody ProductRequestDto productRequestDto) {
        return productService.registProduct(userDetails.getUser(), productRequestDto);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ResponseMessage> deleteProduct(@PathVariable Long id,@AuthenticationPrincipal UserDetailsImpl userDetails) {
        productService.deleteProductById(id,userDetails.getUser());
        return ResponseMessage.SuccessResponse("상품 삭제가 되었습니다.","");
    }
}