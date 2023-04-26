package com.sparta.village.domain.user.service;

import com.sparta.village.domain.image.service.ImageStorageService;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.reservation.repository.ReservationRepository;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.repository.UserRepository;
import com.sparta.village.domain.zzim.entity.Zzim;
import com.sparta.village.domain.zzim.repository.ZzimRepository;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ZzimRepository zzimRepository;
    @Mock
    private ImageStorageService imageStorageService;
    @Test
    @DisplayName("닉네임 변경-정상케이스")
    public void testUpdateNickname() {
        //given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필")
                .build();

        String newNickname = "새로운닉네임";
        when(userRepository.save(user)).thenReturn(user);

        //when
        ResponseEntity<ResponseMessage> response = userService.updateNickname(newNickname, user);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("변경 완료되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());

        //verify
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("닉네임 변경-오류케이스")
    public void testUpdateNicknameisNull() {
        //given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필")
                .build();

        String newNickname = null;

        //when
        CustomException exception = assertThrows(
                CustomException.class,
                () -> userService.updateNickname(newNickname, user),
                "CustomException throw 안시킴"
        );

        //then
        assertEquals(ErrorCode.BAD_NICKNAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("닉네임 변경-정상케이스")
    public void testUpdateNicknameisDuplicate() {
        //given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("새로운닉네임")
                .profile("프로필")
                .build();

        String newNickname = "새로운닉네임";
        when(userRepository.findByNickname(newNickname)).thenReturn(Optional.of(user));

        //when
        CustomException exception = assertThrows(
                CustomException.class,
                () -> userService.updateNickname(newNickname, user),
                "CustomException throw 안시킴"
        );

        //then
        assertEquals(ErrorCode.DUPLICATE_NICKNAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("마이리스트-제품")
    public void testGetUserMyProductList() {
        //given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필")
                .build();

        Product product = Product.builder()
                .id(1L)
                .title("제목")
                .description("내용")
                .price(15000)
                .location("서울 강남구")
                .zzimCount(0)
                .build();

        List<Product> productList = new ArrayList<>();
        productList.add(product);

        when(productRepository.findAllByUser(user)).thenReturn(productList);
        when(imageStorageService.getFirstImageUrlByProductId(anyLong())).thenReturn("primeImageUrl");

        ResponseEntity<ResponseMessage> response = userService.getUserItemList(user, "products");

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("조회가 완료되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());

        //verify
        verify(productRepository).findAllByUser(user);
        verify(imageStorageService).getFirstImageUrlByProductId(anyLong());
    }

    @Test
    @DisplayName("마이리스트-대여")
    public void testGetUserMyReservationList() {
        //given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필")
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .startDate(LocalDate.ofEpochDay(2023- 1 - 1))
                .endDate(LocalDate.ofEpochDay(2023- 1 - 2))
                .status("waiting")
                .product(Product.builder()
                        .id(1L)
                        .title("제목")
                        .description("내용")
                        .price(15000)
                        .location("서울 강남구")
                        .zzimCount(0)
                        .build())
                .build();

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(reservation);

        when(reservationRepository.findByUser(user)).thenReturn(reservationList);
        when(imageStorageService.getFirstImageUrlByProductId(anyLong())).thenReturn("primeImageUrl");

        ResponseEntity<ResponseMessage> response = userService.getUserItemList(user, "rents");

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("조회가 완료되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());

        //verify
        verify(reservationRepository).findByUser(user);
        verify(imageStorageService).getFirstImageUrlByProductId(anyLong());
    }

    @Test
    @DisplayName("마이리스트-찜")
    public void testGetUserMyZzimList() {
        //given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필")
                .build();

        Zzim zzim = Zzim.builder()
                .id(1L)
                .product(Product.builder()
                                .id(1L)
                                .title("제목")
                                .description("내용")
                                .price(15000)
                                .location("서울 강남구")
                                .zzimCount(0)
                                .build())
                .build();

        List<Zzim> zzimList = new ArrayList<>();
        zzimList.add(zzim);

        when(zzimRepository.findByUser(user)).thenReturn(zzimList);
        when(imageStorageService.getFirstImageUrlByProductId(anyLong())).thenReturn("primeImageUrl");

        ResponseEntity<ResponseMessage> response = userService.getUserItemList(user, "products");

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("조회가 완료되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());

        //verify
        verify(zzimRepository).findByUser(user);
        verify(imageStorageService).getFirstImageUrlByProductId(anyLong());
    }

    @Test
    @DisplayName("마이리스트-productListisNull")
    public void testGetUserMyProductListIsNull() {
        //given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필")
                .build();

        //when
        CustomException exception = assertThrows(
                CustomException.class,
                () -> userService.getUserItemList(user, "anyKey"),
                "CustomException throw 안시킴"
        );

        //then
        assertEquals(ErrorCode.BAD_PARAMETER, exception.getErrorCode());
    }

    @Test
    @DisplayName("getUserByUserId")
    public void testGetUserByUserId() {
        //given
        String userId = "1";
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필")
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        userService.getUserByUserId(userId);

        verify(userRepository).findById(user.getId());
    }

    @Test
    @DisplayName("getUserByNickname-정상케이스")
    public void testGetUserByNickName() {
        String nickname = "닉네임";
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필")
                .build();

        when(userRepository.findByNickname(nickname)).thenReturn(Optional.of(user));

        userRepository.findByNickname(nickname);

        verify(userRepository).findByNickname(nickname);
    }

    @Test
    @DisplayName("getUserByNickname-오류케이스")
    public void testGetUserByNickNameIsEmpty() {
        String nickname = "다른닉네임";

        when(userRepository.findByNickname(nickname)).thenReturn(Optional.empty());

        CustomException exception = assertThrows(
                CustomException.class,
                () -> userService.getUserByNickname(nickname),
                "CustomException throw 안시킴"
        );

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("updateProfileIfNeeded")
    public void testUpdateProfileIfNeeded() {
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필")
                .build();

        String newProfile1 = "새로운 프로필";
        userService.updateProfileIfNeeded(user, newProfile1);

        assertEquals(newProfile1, user.getProfile());
        verify(userRepository, times(1)).saveAndFlush(user);

        userService.updateProfileIfNeeded(user, newProfile1);

        assertEquals(newProfile1, user.getProfile());
        verify(userRepository, times(1)).saveAndFlush(user);
    }
}