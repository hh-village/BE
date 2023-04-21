package com.sparta.village.domain.zzim.service;

import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.zzim.entity.Zzim;
import com.sparta.village.domain.zzim.repository.ZzimRepository;
import com.sparta.village.global.exception.ResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class ZzimServiceTest {
    @InjectMocks
    private ZzimService zzimService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ZzimRepository zzimRepository;

    @Test
    @DisplayName("찜하기")
    public void testZzimFunction() {
        //given
        User user = new User();
        Product product = new Product();
        Zzim zzim = new Zzim(user, product);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(zzimService.getZzim(product, user)).thenReturn(false);
        when(zzimRepository.save(zzim)).thenReturn(zzim);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        //when
        ResponseEntity<ResponseMessage> response = zzimService.zzim(1L, user);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("찜하기 성공", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(false, response.getBody().getData());

        //verify
        verify(productRepository).findById(1L);
        verify(zzimRepository).save(any(Zzim.class));
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("찜하기 취소")
    public void testZzimCancel() {
        //given
        User user = new User();
        Product product = new Product();
        Zzim zzim = new Zzim(user, product);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(zzimService.getZzim(product, user)).thenReturn(true);
        when(zzimRepository.findByProductAndUser(product, user)).thenReturn(zzim);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        //when
        ResponseEntity<ResponseMessage> response = zzimService.zzim(1L, user);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("찜하기 취소", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals(true, response.getBody().getData());

        //verify
        verify(productRepository).findById(1L);
        verify(zzimRepository).delete(any(Zzim.class));
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("찜상태-UserIsNull")
    public void testGetZzimStatus() {
        // Given
        User user = null;
        Product product = new Product();

        when(zzimRepository.existsByProductAndUser(product, null)).thenReturn(false);

        // When
        boolean zzimStatus = zzimService.getZzimStatus(user, product);

        // Then
        assertFalse(zzimStatus);
    }

    @Test
    @DisplayName("찜 카운트-정상")
    public void testZzimCount() {
        // Given
        User user = new User();

        when(zzimRepository.countByUser(user)).thenReturn(5);

        // When
        int zzimCount = zzimService.getZzimCount(user);

        // Then
        assertEquals(5, zzimCount);
        verify(zzimRepository).countByUser(user);
    }

    @Test
    @DisplayName("찜 카운트-UserIsNull")
    public void testZzimCountUserIsNull() {
        //given
        User user = null;

        // When
        int zzimCount = zzimService.getZzimCount(user);

        // Then
        assertEquals(0, zzimCount);
        verify(zzimRepository, never()).countByUser(any(User.class));
    }

    public int getZzimCount(User user) {
        return user != null ? zzimRepository.countByUser(user) : 0;
    }
}

