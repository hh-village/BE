package com.sparta.village.domain.product.service;

import com.sparta.village.domain.chat.service.ChatRoomService;
import com.sparta.village.domain.image.service.ImageStorageService;
import com.sparta.village.domain.product.dto.ProductDetailResponseDto;
import com.sparta.village.domain.product.dto.ProductRequestDto;
import com.sparta.village.domain.product.dto.ProductResponseDto;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.dto.AcceptReservationResponseDto;
import com.sparta.village.domain.reservation.dto.ReservationCountResponseDto;
import com.sparta.village.domain.reservation.dto.ReservationResponseDto;
import com.sparta.village.domain.reservation.service.ReservationService;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.entity.UserRoleEnum;
import com.sparta.village.domain.user.service.UserService;
import com.sparta.village.domain.zzim.service.ZzimService;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ImageStorageService imageStorageService;
    @Mock
    private ChatRoomService chatRoomService;
    @Mock
    private ReservationService reservationService;
    @Mock
    private ZzimService zzimService;
    @Mock
    private UserService userService;
    @Mock
    private UserDetailsImpl userDetails;

    @Test
    @DisplayName("제품등록-정상케이스")
    public void testRegistProduct() {
        // given
        User user = new User(12345L, "테스트 닉네임", "profile.jpg", UserRoleEnum.USER);

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .title("제품명")
                .description("제품 설명")
                .price(10000)
                .location("서울")
                .build();

        Product newProduct = new Product(user, productRequestDto);

        when(productRepository.saveAndFlush(any(Product.class))).thenReturn(newProduct);

        // When
        ResponseEntity<ResponseMessage> response = productService.registProduct(user, productRequestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("성공적으로 제품 등록이 되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());

        // Verify
        verify(productRepository, times(1)).saveAndFlush(any(Product.class));
    }

    @Test
    @DisplayName("제품삭제-정상케이스")
    public void testDeleteProduct() {
        // given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .title("제품명")
                .description("제품 설명")
                .price(10000)
                .location("서울")
                .build();

        Product newProduct = new Product(user, productRequestDto);
        Long productId = newProduct.getId();

        when(productRepository.findById(productId)).thenReturn(Optional.of(newProduct));
        doNothing().when(chatRoomService).deleteMessagesByProductId(productId);
        doNothing().when(chatRoomService).deleteByProductId(productId);
        doNothing().when(reservationService).deleteByProductId(productId);
        doNothing().when(zzimService).deleteByProductId(productId);
        doNothing().when(imageStorageService).deleteImagesByProductId(productId);
        doNothing().when(productRepository).deleteById(productId);

        // When
        ResponseEntity<ResponseMessage> response = productService.deleteProduct(productId, user);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("상품 삭제가 되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).deleteById(productId);
        verify(chatRoomService, times(1)).deleteMessagesByProductId(productId);
        verify(chatRoomService, times(1)).deleteByProductId(productId);
        verify(reservationService, times(1)).deleteByProductId(productId);
        verify(zzimService, times(1)).deleteByProductId(productId);
        verify(imageStorageService, times(1)).deleteImagesByProductId(productId);
    }

    @Test
    @DisplayName("제품삭제-권한없음")
    public void testDeleteProductNotAUTHOR() {
        // Given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        User anotherUser = User.builder()
                .id(2L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .title("제품명")
                .description("제품 설명")
                .price(10000)
                .location("서울")
                .build();

        Product product = new Product(anotherUser, productRequestDto);

        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // when
        CustomException expectedException = assertThrows(
                CustomException.class,
                () -> productService.deleteProduct(product.getId(), user),
                "CustomException이 발생할 것으로 예상했지만 throw되지 않았습니다."
        );

        // then
        assertEquals(ErrorCode.NOT_AUTHOR, expectedException.getErrorCode());
    }

    @Test
    @DisplayName("제품수정-정상 케이스")
    public void testUpdateProduct() {
        // Given
        Long productId = 1L;
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .title("제품명")
                .description("제품 설명")
                .price(10000)
                .location("서울")
                .build();

        Product product = new Product(user, productRequestDto);
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // When
        ResponseEntity<ResponseMessage> response = productService.updateProduct(productId, user, productRequestDto);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("상품 수정이 되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());

        // Verify
        verify(imageStorageService, times(1)).deleteImagesByProductId(productId);
    }

    @Test
    @DisplayName("제품수정-권한없음")
    public void testUpdateProductNotAUTHOR() {
        // Given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        User anotherUser = User.builder()
                .id(2L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .title("제품명")
                .description("제품 설명")
                .price(10000)
                .location("서울")
                .build();

        Product product = new Product(anotherUser, productRequestDto);

        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));

        // when
        CustomException expectedException = assertThrows(
                CustomException.class,
                () -> productService.updateProduct(product.getId(), user, productRequestDto),
                "CustomException이 발생할 것으로 예상했지만 throw되지 않았습니다."
        );

        // then
        assertEquals(ErrorCode.NOT_AUTHOR, expectedException.getErrorCode());
    }

    @Test
    public void testDetailProduct() {
        // Given
        Long productId = 1L;
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .title("제품명")
                .description("제품 설명")
                .price(10000)
                .location("서울")
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(user, "123");

        Product product = new Product(userDetails.getUser(), productRequestDto);

        User owner = User.builder()
                .nickname("owner")
                .profile("profile")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userService.getUserByUserId(Long.toString(product.getUser().getId()))).thenReturn(owner);
        when(zzimService.countByProductId(productId)).thenReturn(2);
        List<String> imageUrls = Arrays.asList("url1", "url2");
        when(imageStorageService.getImageUrlListByProductId(productId)).thenReturn(imageUrls);
        List<ReservationResponseDto> reservationList = new ArrayList<>();
        when(reservationService.getReservationList(user, productId)).thenReturn(reservationList);

        // When
        ResponseEntity<ResponseMessage> response = productService.detailProduct(userDetails, productId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("제품 조회가 완료되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
        ProductDetailResponseDto productDetailResponseDto = (ProductDetailResponseDto) response.getBody().getData();
        assertEquals(product.getId(), productDetailResponseDto.getId());
        assertFalse(productDetailResponseDto.isCheckOwner());
        assertFalse(productDetailResponseDto.isZzimStatus());
        assertEquals(2, productDetailResponseDto.getZzimCount());
        assertEquals(imageUrls, productDetailResponseDto.getImageList());
        assertEquals("owner", productDetailResponseDto.getOwnerNickname());
        assertEquals("profile", productDetailResponseDto.getProfile());
        assertEquals(reservationList, productDetailResponseDto.getReservationList());

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verify(userService, times(1)).getUserByUserId(Long.toString(product.getUser().getId()));
        verify(zzimService, times(1)).countByProductId(productId);
        verify(imageStorageService, times(1)).getImageUrlListByProductId(productId);
        verify(reservationService, times(1)).getReservationList(user, productId);
    }

    @Test
    @DisplayName("제품상세조회-유저Null")
    public void testDetailProductUserIsNull() {
        // given
        Long productId = 1L;
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .title("제품명")
                .description("제품 설명")
                .price(10000)
                .location("서울")
                .build();

        UserDetailsImpl userDetails = null;

        Product product = new Product(user, productRequestDto);

        User owner = User.builder()
                .nickname("owner")
                .profile("profile")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userService.getUserByUserId(Long.toString(product.getUser().getId()))).thenReturn(owner);
        when(zzimService.countByProductId(productId)).thenReturn(2);
        List<String> imageUrls = Arrays.asList("url1", "url2");
        when(imageStorageService.getImageUrlListByProductId(productId)).thenReturn(imageUrls);
        List<ReservationResponseDto> reservationList = new ArrayList<>();
        when(reservationService.getReservationList(null, productId)).thenReturn(reservationList);

        // When
        ResponseEntity<ResponseMessage> response = productService.detailProduct(userDetails, productId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("제품 조회가 완료되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
        ProductDetailResponseDto productDetailResponseDto = (ProductDetailResponseDto) response.getBody().getData();
        assertEquals(product.getId(), productDetailResponseDto.getId());
        assertFalse(productDetailResponseDto.isCheckOwner());
        assertFalse(productDetailResponseDto.isZzimStatus());
        assertEquals(2, productDetailResponseDto.getZzimCount());
        assertEquals(imageUrls, productDetailResponseDto.getImageList());
        assertEquals("owner", productDetailResponseDto.getOwnerNickname());
        assertEquals("profile", productDetailResponseDto.getProfile());
        assertEquals(reservationList, productDetailResponseDto.getReservationList());

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verify(userService, times(1)).getUserByUserId(Long.toString(product.getUser().getId()));
        verify(zzimService, times(1)).countByProductId(productId);
        verify(imageStorageService, times(1)).getImageUrlListByProductId(productId);
        verify(reservationService, times(1)).getReservationList(null, productId);
    }

    @Test
    @DisplayName("제품상세조회-OwnerIsTrue")
    public void testDetailOwnerIsTrue() {
        // given
        Long productId = 1L;
        Long userId = 1L;
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .title("제품명")
                .description("제품 설명")
                .price(10000)
                .location("서울")
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(user, "123");

        Product product = new Product(userDetails.getUser(), productRequestDto);

        User owner = User.builder()
                .nickname("owner")
                .profile("profile")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userService.getUserByUserId(Long.toString(product.getUser().getId()))).thenReturn(owner);
        when(productService.checkProductOwner(productId, userId)).thenReturn(true);
        when(zzimService.countByProductId(productId)).thenReturn(2);
        List<String> imageUrls = Arrays.asList("url1", "url2");
        when(imageStorageService.getImageUrlListByProductId(productId)).thenReturn(imageUrls);
        List<ReservationResponseDto> reservationList = new ArrayList<>();
        when(reservationService.getReservationList(user, productId)).thenReturn(reservationList);

        // When
        ResponseEntity<ResponseMessage> response = productService.detailProduct(userDetails, productId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("제품 조회가 완료되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
        ProductDetailResponseDto productDetailResponseDto = (ProductDetailResponseDto) response.getBody().getData();
        assertEquals(product.getId(), productDetailResponseDto.getId());
        assertTrue(productDetailResponseDto.isCheckOwner());
        assertFalse(productDetailResponseDto.isZzimStatus());
        assertEquals(2, productDetailResponseDto.getZzimCount());
        assertEquals(imageUrls, productDetailResponseDto.getImageList());
        assertEquals("owner", productDetailResponseDto.getOwnerNickname());
        assertEquals("profile", productDetailResponseDto.getProfile());
        assertEquals(reservationList, productDetailResponseDto.getReservationList());

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verify(userService, times(1)).getUserByUserId(Long.toString(product.getUser().getId()));
        verify(zzimService, times(1)).countByProductId(productId);
        verify(imageStorageService, times(1)).getImageUrlListByProductId(productId);
        verify(reservationService, times(1)).getReservationList(user, productId);
    }

    @Test
    @DisplayName("제품상세조회-ZzimIsTrue")
    public void testDetailZzimIsTrue() {
        // given
        Long productId = 1L;
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .title("제품명")
                .description("제품 설명")
                .price(10000)
                .location("서울")
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(user, "123");

        Product product = new Product(userDetails.getUser(), productRequestDto);

        User owner = User.builder()
                .nickname("owner")
                .profile("profile")
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(userService.getUserByUserId(Long.toString(product.getUser().getId()))).thenReturn(owner);
        when(zzimService.getZzimStatus(user, product)).thenReturn(true);
        when(zzimService.countByProductId(productId)).thenReturn(2);
        List<String> imageUrls = Arrays.asList("url1", "url2");
        when(imageStorageService.getImageUrlListByProductId(productId)).thenReturn(imageUrls);
        List<ReservationResponseDto> reservationList = new ArrayList<>();
        when(reservationService.getReservationList(user, productId)).thenReturn(reservationList);

        // When
        ResponseEntity<ResponseMessage> response = productService.detailProduct(userDetails, productId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("제품 조회가 완료되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
        ProductDetailResponseDto productDetailResponseDto = (ProductDetailResponseDto) response.getBody().getData();
        assertEquals(product.getId(), productDetailResponseDto.getId());
        assertFalse(productDetailResponseDto.isCheckOwner());
        assertTrue(productDetailResponseDto.isZzimStatus());
        assertEquals(2, productDetailResponseDto.getZzimCount());
        assertEquals(imageUrls, productDetailResponseDto.getImageList());
        assertEquals("owner", productDetailResponseDto.getOwnerNickname());
        assertEquals("profile", productDetailResponseDto.getProfile());
        assertEquals(reservationList, productDetailResponseDto.getReservationList());

        // Verify
        verify(productRepository, times(1)).findById(productId);
        verify(userService, times(1)).getUserByUserId(Long.toString(product.getUser().getId()));
        verify(zzimService, times(1)).countByProductId(productId);
        verify(imageStorageService, times(1)).getImageUrlListByProductId(productId);
        verify(reservationService, times(1)).getReservationList(user, productId);
    }

    @Test
    @DisplayName("제품검색-key값 둘다 입력한 케이스")
    public void testKeyQueryAndLocationSearchProductList() {
        // Given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        Product product = Product.builder()
                .id(1L)
                .title("제목")
                .description("내용")
                .price(15000)
                .location("서울 강남구")
                .zzimCount(3)
                .build();

        when(userDetails.getUser()).thenReturn(user);

        List<String> mockImageUrlList = Collections.singletonList("https://example.com/image.jpg");
        when(imageStorageService.getImageUrlListByProductId(anyLong())).thenReturn(mockImageUrlList);

        List<Product> mockProductList = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(mockProductList);
        when(productRepository.findByLocationContaining(anyString())).thenReturn(mockProductList);
        when(productRepository.findByTitleContaining(anyString())).thenReturn(mockProductList);
        when(productRepository.findByTitleContainingAndLocationContaining(anyString(), anyString())).thenReturn(mockProductList);

        String mockImageUrl = "https://example.com/image.jpg";
        when(zzimService.getZzimStatus(user, product)).thenReturn(false);

        // When
        ResponseEntity<ResponseMessage> response = productService.searchProductList(userDetails, "qr", "location");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("검색 조회가 되었습니다.", response.getBody().getMessage());
        List<ProductResponseDto> responseList = (List<ProductResponseDto>) response.getBody().getData();
        assertEquals(mockImageUrl, responseList.get(0).getImage());

        // Verify
        verify(productRepository).findByTitleContainingAndLocationContaining("qr", "location");
        verify(zzimService).getZzimStatus(user, product);
    }

    @Test
    @DisplayName("제품검색-key값 둘다 Null 케이스")
    void testKeyNullAndNullSearchProductList() {
        // Given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        Product product = Product.builder()
                .id(1L)
                .title("제목")
                .description("내용")
                .price(15000)
                .location("서울 강남구")
                .zzimCount(3)
                .build();

        when(userDetails.getUser()).thenReturn(user);

        List<String> mockImageUrlList = Collections.singletonList("https://example.com/image.jpg");
        when(imageStorageService.getImageUrlListByProductId(anyLong())).thenReturn(mockImageUrlList);

        List<Product> mockProductList = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(mockProductList);
        when(productRepository.findByLocationContaining(anyString())).thenReturn(mockProductList);
        when(productRepository.findByTitleContaining(anyString())).thenReturn(mockProductList);
        when(productRepository.findByTitleContainingAndLocationContaining(anyString(), anyString())).thenReturn(mockProductList);

        String mockImageUrl = "https://example.com/image.jpg";
        when(zzimService.getZzimStatus(user, product)).thenReturn(false);

        // When
        ResponseEntity<ResponseMessage> response = productService.searchProductList(userDetails, null, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("검색 조회가 되었습니다.", response.getBody().getMessage());
        List<ProductResponseDto> responseList = (List<ProductResponseDto>) response.getBody().getData();
        assertEquals(mockImageUrl, responseList.get(0).getImage());

        // Verify
        verify(productRepository).findAll();
        verify(zzimService).getZzimStatus(user, product);
    }

    @Test
    @DisplayName("제품검색-UserIsNull")
    void testUserIsNullSearchProductList() {
        // Given
        UserDetailsImpl userDetails = null;

        Product product = Product.builder()
                .id(1L)
                .title("제목")
                .description("내용")
                .price(15000)
                .location("서울 강남구")
                .zzimCount(3)
                .build();

        List<String> mockImageUrlList = Collections.singletonList("https://example.com/image.jpg");
        when(imageStorageService.getImageUrlListByProductId(anyLong())).thenReturn(mockImageUrlList);

        List<Product> mockProductList = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(mockProductList);
        when(productRepository.findByLocationContaining(anyString())).thenReturn(mockProductList);
        when(productRepository.findByTitleContaining(anyString())).thenReturn(mockProductList);
        when(productRepository.findByTitleContainingAndLocationContaining(anyString(), anyString())).thenReturn(mockProductList);

        String mockImageUrl = "https://example.com/image.jpg";
        when(zzimService.getZzimStatus(null, product)).thenReturn(false);

        // When
        ResponseEntity<ResponseMessage> response = productService.searchProductList(userDetails, null, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("검색 조회가 되었습니다.", response.getBody().getMessage());
        List<ProductResponseDto> responseList = (List<ProductResponseDto>) response.getBody().getData();
        assertEquals(mockImageUrl, responseList.get(0).getImage());

        // Verify
        verify(productRepository).findAll();
        verify(zzimService).getZzimStatus(null, product);
    }

    @Test
    @DisplayName("제품검색-key값 qr==Null 케이스")
    void testKeyNullAndLocationSearchProductList() {
        // Given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        Product product = Product.builder()
                .id(1L)
                .title("제목")
                .description("내용")
                .price(15000)
                .location("서울 강남구")
                .zzimCount(3)
                .build();

        when(userDetails.getUser()).thenReturn(user);

        List<String> mockImageUrlList = Collections.singletonList("https://example.com/image.jpg");
        when(imageStorageService.getImageUrlListByProductId(anyLong())).thenReturn(mockImageUrlList);

        List<Product> mockProductList = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(mockProductList);
        when(productRepository.findByLocationContaining(anyString())).thenReturn(mockProductList);
        when(productRepository.findByTitleContaining(anyString())).thenReturn(mockProductList);
        when(productRepository.findByTitleContainingAndLocationContaining(anyString(), anyString())).thenReturn(mockProductList);

        String mockImageUrl = "https://example.com/image.jpg";
        when(zzimService.getZzimStatus(user, product)).thenReturn(false);

        // When
        ResponseEntity<ResponseMessage> response = productService.searchProductList(userDetails, null, "location");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("검색 조회가 되었습니다.", response.getBody().getMessage());
        List<ProductResponseDto> responseList = (List<ProductResponseDto>) response.getBody().getData();
        assertEquals(mockImageUrl, responseList.get(0).getImage());

        // Verify
        verify(productRepository).findByLocationContaining("location");
        verify(zzimService).getZzimStatus(user, product);
    }

    @Test
    @DisplayName("제품검색-key값 location==Null 케이스")
    void testKeyQrAndNullSearchProductList() {
        // Given
        User user = User.builder()
                .id(1L)
                .kakaoId(123L)
                .nickname("닉네임")
                .profile("프로필.jpg")
                .role(UserRoleEnum.USER)
                .build();

        Product product = Product.builder()
                .id(1L)
                .title("제목")
                .description("내용")
                .price(15000)
                .location("서울 강남구")
                .zzimCount(3)
                .build();

        when(userDetails.getUser()).thenReturn(user);

        List<String> mockImageUrlList = Collections.singletonList("https://example.com/image.jpg");
        when(imageStorageService.getImageUrlListByProductId(anyLong())).thenReturn(mockImageUrlList);

        List<Product> mockProductList = Arrays.asList(product);
        when(productRepository.findAll()).thenReturn(mockProductList);
        when(productRepository.findByLocationContaining(anyString())).thenReturn(mockProductList);
        when(productRepository.findByTitleContaining(anyString())).thenReturn(mockProductList);
        when(productRepository.findByTitleContainingAndLocationContaining(anyString(), anyString())).thenReturn(mockProductList);

        String mockImageUrl = "https://example.com/image.jpg";
        when(zzimService.getZzimStatus(user, product)).thenReturn(false);

        // When
        ResponseEntity<ResponseMessage> response = productService.searchProductList(userDetails, "qr", null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("검색 조회가 되었습니다.", response.getBody().getMessage());
        List<ProductResponseDto> responseList = (List<ProductResponseDto>) response.getBody().getData();
        assertEquals(mockImageUrl, responseList.get(0).getImage());

        // Verify
        verify(productRepository).findByTitleContaining("qr");
        verify(zzimService).getZzimStatus(user, product);
    }


    @Test
    @DisplayName("인기제품-True")
    void testGetMostProductTrue() {
        // Given
        Product product = Product.builder()
                .id(1L)
                .title("제목")
                .description("내용")
                .price(15000)
                .location("서울 강남구")
                .zzimCount(3)
                .build();

        ReservationCountResponseDto countResponseDto = new ReservationCountResponseDto(1L, 5L);
        List<ReservationCountResponseDto> reservationCounts = Collections.singletonList(countResponseDto);

        when(reservationService.reservationCount()).thenReturn(reservationCounts);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        // When
        boolean isMostProduct = productService.isMostProduct(product);

        // Then
        assertTrue(isMostProduct);

        // Verify
        verify(reservationService).reservationCount();
        verify(productRepository).findById(countResponseDto.getProductId());
    }

    @Test
    @DisplayName("인기제품-False")
    void testGetMostProductFalse() {
        // Given
        Product product = Product.builder()
                .id(1L)
                .title("제목")
                .description("내용")
                .price(15000)
                .location("서울 강남구")
                .zzimCount(2)
                .build();

        ReservationCountResponseDto countResponseDto = new ReservationCountResponseDto(2L, 40L);
        List<ReservationCountResponseDto> reservationCounts = Collections.singletonList(countResponseDto);

        when(reservationService.reservationCount()).thenReturn(reservationCounts);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // When
        boolean isMostProduct = productService.isMostProduct(product);

        // Then
        assertFalse(isMostProduct);

        // Verify
        verify(reservationService).reservationCount();
        verify(productRepository).findById(2L);
    }

    @Test
    @DisplayName("전체조회-정상")
    public void testGetMainPage() {
        // given
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        User user = mock(User.class);
        when(userDetails.getUser()).thenReturn(user);

        List<AcceptReservationResponseDto> dealList = new ArrayList<>();
        when(reservationService.getAcceptedReservationList()).thenReturn(dealList);

        List<Product> randomProducts1 = new ArrayList<>();
        List<Product> randomProducts2 = new ArrayList<>();
        when(productRepository.findRandomProduct(8)).thenReturn(randomProducts1);
        when(productRepository.findRandomProduct(6)).thenReturn(randomProducts2);

        when(imageStorageService.getImageUrlListByProductId(anyLong())).thenReturn(Arrays.asList("image_url"));
        when(zzimService.getZzimStatus(any(User.class), any(Product.class))).thenReturn(false);
        when(zzimService.getZzimCount(user)).thenReturn(0);

        // when
        ResponseEntity<ResponseMessage> response = productService.getMainPage(userDetails);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("메인페이지 조회되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());

        // verify
        verify(reservationService, times(1)).getAcceptedReservationList();
        verify(productRepository, times(1)).findRandomProduct(8);
        verify(productRepository, times(1)).findRandomProduct(6);
        verify(imageStorageService, times(randomProducts1.size() + randomProducts2.size())).getImageUrlListByProductId(anyLong());
        verify(zzimService, times(randomProducts1.size() + randomProducts2.size())).getZzimStatus(any(User.class), any(Product.class));
        verify(zzimService, times(1)).getZzimCount(user);
    }

    @Test
    @DisplayName("전체조회-UserIsNull")
    public void testGetMainPageUserIsNull() {
        // given
        UserDetailsImpl userDetails = null;

        List<AcceptReservationResponseDto> dealList = new ArrayList<>();
        when(reservationService.getAcceptedReservationList()).thenReturn(dealList);

        List<Product> randomProducts1 = new ArrayList<>();
        List<Product> randomProducts2 = new ArrayList<>();
        when(productRepository.findRandomProduct(8)).thenReturn(randomProducts1);
        when(productRepository.findRandomProduct(6)).thenReturn(randomProducts2);

        when(imageStorageService.getImageUrlListByProductId(anyLong())).thenReturn(Arrays.asList("image_url"));

        // when
        ResponseEntity<ResponseMessage> response = productService.getMainPage(userDetails);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("메인페이지 조회되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());

        // verify
        verify(reservationService, times(1)).getAcceptedReservationList();
        verify(productRepository, times(1)).findRandomProduct(8);
        verify(productRepository, times(1)).findRandomProduct(6);
        verify(imageStorageService, times(randomProducts1.size() + randomProducts2.size())).getImageUrlListByProductId(anyLong());
        verify(zzimService, times(0)).getZzimStatus(any(User.class), any(Product.class));
        verify(zzimService, times(0)).getZzimCount(any(User.class));
    }

    @Test
    @DisplayName("상품 ID로 상품 조회 성공")
    void testFindProductByIdSuccess() {
// Given
        Long id = 1L;
        Product product = Product.builder()
                .id(id)
                .title("제목")
                .description("내용")
                .price(15000)
                .location("서울 강남구")
                .zzimCount(2)
                .build();

        when(productRepository.findById(id)).thenReturn(Optional.of(product));

        // When
        Product foundProduct = productService.findProductById(id);

        // Then
        assertAll(
                () -> assertNotNull(foundProduct),
                () -> assertEquals(product.getId(), foundProduct.getId()),
                () -> assertEquals(product.getTitle(), foundProduct.getTitle()),
                () -> assertEquals(product.getDescription(), foundProduct.getDescription()),
                () -> assertEquals(product.getPrice(), foundProduct.getPrice()),
                () -> assertEquals(product.getLocation(), foundProduct.getLocation()),
                () -> assertEquals(product.getZzimCount(), foundProduct.getZzimCount())
        );

        verify(productRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("상품 ID로 상품 조회 실패")
    void testFindProductByIdFailure() {
        // Given
        Long id = 1L;
        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // When, Then
        CustomException expectedException = assertThrows(
                CustomException.class,
                () -> productService.findProductById(id),
                "CustomException이 발생할 것으로 예상했지만 throw되지 않았습니다."
        );

        //then
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, expectedException.getErrorCode());
    }
}

