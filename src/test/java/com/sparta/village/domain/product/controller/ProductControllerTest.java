package com.sparta.village.domain.product.controller;

import com.sparta.village.domain.product.dto.ProductRequestDto;
import com.sparta.village.domain.product.service.ProductService;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.entity.UserRoleEnum;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {
    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    public void testGetMainPage() {
        // given
        ResponseMessage responseMessage = new ResponseMessage("메인페이지 조회되었습니다.", 200, new Object());
        when(productService.getMainPage(any())).thenReturn(ResponseEntity.ok(responseMessage));

        // when
        ResponseEntity<ResponseMessage> result = productController.getMainPage(null);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseMessage, result.getBody());
        verify(productService).getMainPage(any());
    }

    @Test
    public void testSearchProductList() {
        //given
        ResponseMessage responseMessage = new ResponseMessage("검색 조회가 되었습니다.", 200, new Object());
        when(productService.searchProductList(null, "name", "location", 13L, 2)).thenReturn(ResponseEntity.ok(responseMessage));

        // when
        ResponseEntity<ResponseMessage> result = productController.searchProductList(null, "name", "location", 13L, 2);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseMessage, result.getBody());
        verify(productService).searchProductList(null, "name", "location", 13L, 2);
    }

    @Test
    public void testDetailProduct() {
        // given
        ResponseMessage responseMessage = new ResponseMessage("Product detail retrieved successfully.", 200, new Object());
        when(productService.detailProduct(any(), anyLong())).thenReturn(ResponseEntity.ok(responseMessage));

        // when
        ResponseEntity<ResponseMessage> result = productController.detailProduct(null, 1L);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseMessage, result.getBody());
        verify(productService).detailProduct(any(), eq(1L));
    }

    @Test
    public void testRegistProduct() {
        // given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, "123L");

        ResponseMessage responseMessage = new ResponseMessage("Product registered successfully.", 200, new Object());
        ProductRequestDto productRequestDto = new ProductRequestDto();
        when(productService.registProduct(any(), any())).thenReturn(ResponseEntity.ok(responseMessage));

        // when
        ResponseEntity<ResponseMessage> result = productController.registProduct(userDetails, productRequestDto);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseMessage, result.getBody());
        verify(productService).registProduct(any(), eq(productRequestDto));
    }

    @Test
    public void testUpdateProduct() {
        // given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, "123L");

        ResponseMessage responseMessage = new ResponseMessage("Product updated successfully.", 200, new Object());
        ProductRequestDto productRequestDto = new ProductRequestDto();
        when(productService.updateProduct(anyLong(), any(), any())).thenReturn(ResponseEntity.ok(responseMessage));

        // when
        ResponseEntity<ResponseMessage> result = productController.updateProduct(1L, userDetails, productRequestDto);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseMessage, result.getBody());
        verify(productService).updateProduct(eq(1L), any(), eq(productRequestDto));
    }

    @Test
    public void testDeleteProduct() {
        // given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();
        UserDetailsImpl userDetails = new UserDetailsImpl(user, "123L");

        ResponseMessage responseMessage = new ResponseMessage("Product deleted successfully.", 200, new Object());
        when(productService.deleteProduct(anyLong(), any())).thenReturn(ResponseEntity.ok(responseMessage));

        // when
        ResponseEntity<ResponseMessage> result = productController.deleteProduct(1L, userDetails);

        // then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseMessage, result.getBody());
        verify(productService).deleteProduct(eq(1L), any());
    }

}