//package com.sparta.village.domain.product.service;
//
//import com.sparta.village.domain.chat.service.ChatService;
//import com.sparta.village.domain.image.service.ImageStorageService;
//import com.sparta.village.domain.product.dto.ProductDetailResponseDto;
//import com.sparta.village.domain.product.dto.ProductRequestDto;
//import com.sparta.village.domain.product.entity.Product;
//import com.sparta.village.domain.product.repository.ProductRepository;
//import com.sparta.village.domain.product.repository.SearchQueryRepository;
//import com.sparta.village.domain.reservation.dto.ReservationCountResponseDto;
//import com.sparta.village.domain.reservation.dto.ReservationResponseDto;
//import com.sparta.village.domain.reservation.service.ReservationService;
//import com.sparta.village.domain.user.entity.User;
//import com.sparta.village.domain.user.entity.UserRoleEnum;
//import com.sparta.village.domain.user.service.UserService;
//import com.sparta.village.domain.zzim.service.ZzimService;
//import com.sparta.village.global.exception.CustomException;
//import com.sparta.village.global.exception.ErrorCode;
//import com.sparta.village.global.exception.ResponseMessage;
//import com.sparta.village.global.security.UserDetailsImpl;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.web.multipart.MultipartFile;
//import java.util.*;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class ProductServiceTest {
//
//    @InjectMocks
//    private ProductService productService;
//    @Mock
//    private ProductRepository productRepository;
//    @Mock
//    private SearchQueryRepository searchQueryRepository;
//    @Mock
//    private ImageStorageService imageStorageService;
//    @Mock
//    private ChatService chatService;
//    @Mock
//    private ReservationService reservationService;
//    @Mock
//    private ZzimService zzimService;
//    @Mock
//    private UserService userService;
//    @Mock
//    private UserDetailsImpl userDetails;
//
//    @Test
//    @DisplayName("제품등록-정상케이스")
//    public void testRegistProduct() {
//        // given
//        List<MultipartFile> imageFile = new ArrayList<>();
//        MultipartFile dummyImage1 = new MockMultipartFile("image1", "image1.jpg", "image/jpeg", new byte[10]);
//        MultipartFile dummyImage2 = new MockMultipartFile("image2", "image2.jpg", "image/jpeg", new byte[10]);
//        imageFile.add(dummyImage1);
//        imageFile.add(dummyImage2);
//
//        User user = User.builder()
//                .id(1L)
//                .kakaoId(123L)
//                .nickname("닉네임")
//                .profile("프로필.jpg")
//                .role(UserRoleEnum.USER)
//                .build();
//
//        ProductRequestDto productRequestDto = ProductRequestDto.builder()
//                .title("제품명")
//                .description("제품 설명")
//                .price(10000)
//                .location("서울")
//                .images(imageFile)
//                .build();
//
//        Product product = new Product(user, productRequestDto);
//        List<String> imageUrlList = new ArrayList<>();
//        String imageUrl1 = "imageUrl1";
//        String imageUrl2 = "imageUrl2";
//        imageUrlList.add(imageUrl1);
//        imageUrlList.add(imageUrl2);
//
//        when(productRepository.saveAndFlush(any(Product.class))).thenReturn(product);
//        doNothing().when(imageStorageService).saveImageList(any(Product.class), eq(imageUrlList));
//        when(imageStorageService.storeFiles(anyList())).thenReturn(imageUrlList);
//
//        // When
//        ResponseEntity<ResponseMessage> response = productService.registProduct(user, productRequestDto);
//
//        // Then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("성공적으로 제품 등록이 되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
//
//        // Verify
//        verify(productRepository, times(1)).saveAndFlush(any(Product.class));
//        verify(imageStorageService).saveImageList(any(Product.class), eq(imageUrlList));
//        verify(imageStorageService).storeFiles(anyList());
//    }
//
//    @Test
//    @DisplayName("제품삭제-정상케이스")
//    public void testDeleteProduct() {
//        // given
//        User user = User.builder()
//                .id(1L)
//                .kakaoId(123L)
//                .nickname("닉네임")
//                .profile("프로필.jpg")
//                .role(UserRoleEnum.USER)
//                .build();
//
//        ProductRequestDto productRequestDto = ProductRequestDto.builder()
//                .title("제품명")
//                .description("제품 설명")
//                .price(10000)
//                .location("서울")
//                .build();
//
//        Product newProduct = new Product(user, productRequestDto);
//        Long productId = newProduct.getId();
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(newProduct));
//        doNothing().when(chatService).deleteMessagesByProductId(productId);
//        doNothing().when(chatService).deleteByProductId(productId);
//        doNothing().when(reservationService).deleteByProductId(productId);
//        doNothing().when(zzimService).deleteByProductId(productId);
//        doNothing().when(imageStorageService).deleteImagesByProductId(productId);
//        doNothing().when(productRepository).deleteById(productId);
//
//        // When
//        ResponseEntity<ResponseMessage> response = productService.deleteProduct(productId, user);
//
//        // Then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("상품 삭제가 되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
//
//        // Verify
//        verify(productRepository, times(1)).findById(productId);
//        verify(productRepository, times(1)).deleteById(productId);
//        verify(chatService, times(1)).deleteMessagesByProductId(productId);
//        verify(chatService, times(1)).deleteByProductId(productId);
//        verify(reservationService, times(1)).deleteByProductId(productId);
//        verify(zzimService, times(1)).deleteByProductId(productId);
//        verify(imageStorageService, times(1)).deleteImagesByProductId(productId);
//    }
//
//    @Test
//    @DisplayName("제품삭제-제품없음")
//    void deleteProduct_whenProductNotFound_throwsCustomException() {
//        Long productId = 1L;
//        User user = new User();
//        user.setId(2L);
//
//        when(productRepository.findById(productId)).thenReturn(Optional.empty());
//
//        // Assert that the expected exception is thrown
//        CustomException exception = assertThrows(
//                CustomException.class,
//                () -> productService.deleteProduct(productId, user),
//                "CustomException throw 안시킴"
//        );
//
//        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("제품삭제-권한없음")
//    public void testDeleteProductNotAUTHOR() {
//        // Given
//        User user = User.builder()
//                .id(1L)
//                .kakaoId(123L)
//                .nickname("닉네임")
//                .profile("프로필.jpg")
//                .role(UserRoleEnum.USER)
//                .build();
//
//        User anotherUser = User.builder()
//                .id(2L)
//                .kakaoId(123L)
//                .nickname("닉네임")
//                .profile("프로필.jpg")
//                .role(UserRoleEnum.USER)
//                .build();
//
//        ProductRequestDto productRequestDto = ProductRequestDto.builder()
//                .title("제품명")
//                .description("제품 설명")
//                .price(10000)
//                .location("서울")
//                .build();
//
//        Product product = new Product(anotherUser, productRequestDto);
//
//        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
//
//        // when
//        CustomException expectedException = assertThrows(
//                CustomException.class,
//                () -> productService.deleteProduct(product.getId(), user),
//                "CustomException throw 안시킴"
//        );
//
//        // then
//        assertEquals(ErrorCode.NOT_AUTHOR, expectedException.getErrorCode());
//    }
//    @Test
//    @DisplayName("제품수정-정상 케이스")
//    public void testUpdateProduct() {
//        // Given
//        Long productId = 1L;
//        List<MultipartFile> imageFile = new ArrayList<>();
//        MultipartFile dummyImage1 = new MockMultipartFile("image1", "image1.jpg", "image/jpeg", new byte[10]);
//        MultipartFile dummyImage2 = new MockMultipartFile("image2", "image2.jpg", "image/jpeg", new byte[10]);
//        imageFile.add(dummyImage1);
//        imageFile.add(dummyImage2);
//        User user = User.builder()
//                .id(1L)
//                .kakaoId(123L)
//                .nickname("닉네임")
//                .profile("프로필.jpg")
//                .role(UserRoleEnum.USER)
//                .build();
//
//        ProductRequestDto productRequestDto = ProductRequestDto.builder()
//                .title("제품명")
//                .description("제품 설명")
//                .price(10000)
//                .location("서울")
//                .images(imageFile)
//                .build();
//
//        Product product = new Product(user, productRequestDto);
//
//        List<String> imageUrlList = new ArrayList<>();
//        String imageUrl1 = "imageUrl1";
//        String imageUrl2 = "imageUrl2";
//        imageUrlList.add(imageUrl1);
//        imageUrlList.add(imageUrl2);
//
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        doNothing().when(imageStorageService).deleteImagesByProductId(productId);
//        doNothing().when(imageStorageService).saveImageList(product, imageUrlList);
//        when(imageStorageService.storeFiles(anyList())).thenReturn(imageUrlList);
//
//        // When
//        ResponseEntity<ResponseMessage> response = productService.updateProduct(productId, user, productRequestDto);
//
//        // Then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("상품 수정이 되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
//
//        // Verify
//        verify(productRepository).findById(productId);
//        verify(imageStorageService, times(1)).deleteImagesByProductId(productId);
//        verify(imageStorageService).saveImageList(product, imageUrlList);
//        verify(imageStorageService).storeFiles(anyList());
//    }
//
//    @Test
//    @DisplayName("제품수정-권한없음")
//    public void testUpdateProductNotAUTHOR() {
//        // Given
//        User user = User.builder()
//                .id(1L)
//                .kakaoId(123L)
//                .nickname("닉네임")
//                .profile("프로필.jpg")
//                .role(UserRoleEnum.USER)
//                .build();
//
//        User anotherUser = User.builder()
//                .id(2L)
//                .kakaoId(123L)
//                .nickname("닉네임")
//                .profile("프로필.jpg")
//                .role(UserRoleEnum.USER)
//                .build();
//
//        ProductRequestDto productRequestDto = ProductRequestDto.builder()
//                .title("제품명")
//                .description("제품 설명")
//                .price(10000)
//                .location("서울")
//                .build();
//
//        Product product = new Product(anotherUser, productRequestDto);
//
//        given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
//
//        // when
//        CustomException expectedException = assertThrows(
//                CustomException.class,
//                () -> productService.updateProduct(product.getId(), user, productRequestDto),
//                "CustomException throw 안시킴"
//        );
//
//        // then
//        assertEquals(ErrorCode.NOT_AUTHOR, expectedException.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("상세페이지 조회")
//    public void testDetailProduct() {
//        // Given
//        Long productId = 1L;
//        User user = User.builder()
//                .id(1L)
//                .kakaoId(123L)
//                .nickname("닉네임")
//                .profile("프로필.jpg")
//                .role(UserRoleEnum.USER)
//                .build();
//
//        ProductRequestDto productRequestDto = ProductRequestDto.builder()
//                .title("제품명")
//                .description("제품 설명")
//                .price(10000)
//                .location("서울")
//                .build();
//
//        UserDetailsImpl userDetails = new UserDetailsImpl(user, "123");
//
//        Product product = new Product(userDetails.getUser(), productRequestDto);
//
//        User owner = User.builder()
//                .nickname("owner")
//                .profile("profile")
//                .build();
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(userService.getUserByUserId(Long.toString(product.getUser().getId()))).thenReturn(owner);
//        when(zzimService.countByProductId(productId)).thenReturn(2);
//        List<String> imageUrls = Arrays.asList("url1", "url2");
//        when(imageStorageService.getImageUrlListByProductId(productId)).thenReturn(imageUrls);
//        List<ReservationResponseDto> reservationList = new ArrayList<>();
//        when(reservationService.getReservationList(user, productId)).thenReturn(reservationList);
//
//        // When
//        ResponseEntity<ResponseMessage> response = productService.detailProduct(userDetails, productId);
//
//        // Then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("제품 조회가 완료되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
//        ProductDetailResponseDto productDetailResponseDto = (ProductDetailResponseDto) response.getBody().getData();
//        assertEquals(product.getId(), productDetailResponseDto.getId());
//        assertFalse(productDetailResponseDto.isCheckOwner());
//        assertFalse(productDetailResponseDto.isZzimStatus());
//        assertEquals(2, productDetailResponseDto.getZzimCount());
//        assertEquals(imageUrls, productDetailResponseDto.getImageList());
//        assertEquals("owner", productDetailResponseDto.getOwnerNickname());
//        assertEquals("profile", productDetailResponseDto.getProfile());
//        assertEquals(reservationList, productDetailResponseDto.getReservationList());
//
//        // Verify
//        verify(productRepository, times(1)).findById(productId);
//        verify(userService, times(1)).getUserByUserId(Long.toString(product.getUser().getId()));
//        verify(zzimService, times(1)).countByProductId(productId);
//        verify(imageStorageService, times(1)).getImageUrlListByProductId(productId);
//        verify(reservationService, times(1)).getReservationList(user, productId);
//    }
//
//    @Test
//    @DisplayName("제품상세조회-유저Null")
//    public void testDetailProductUserIsNull() {
//        // given
//        Long productId = 1L;
//        User user = User.builder()
//                .id(1L)
//                .kakaoId(123L)
//                .nickname("닉네임")
//                .profile("프로필.jpg")
//                .role(UserRoleEnum.USER)
//                .build();
//
//        ProductRequestDto productRequestDto = ProductRequestDto.builder()
//                .title("제품명")
//                .description("제품 설명")
//                .price(10000)
//                .location("서울")
//                .build();
//
//        UserDetailsImpl userDetails = null;
//
//        Product product = new Product(user, productRequestDto);
//
//        User owner = User.builder()
//                .nickname("owner")
//                .profile("profile")
//                .build();
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(userService.getUserByUserId(Long.toString(product.getUser().getId()))).thenReturn(owner);
//        when(zzimService.countByProductId(productId)).thenReturn(2);
//        List<String> imageUrls = Arrays.asList("url1", "url2");
//        when(imageStorageService.getImageUrlListByProductId(productId)).thenReturn(imageUrls);
//        List<ReservationResponseDto> reservationList = new ArrayList<>();
//        when(reservationService.getReservationList(null, productId)).thenReturn(reservationList);
//
//        // When
//        ResponseEntity<ResponseMessage> response = productService.detailProduct(userDetails, productId);
//
//        // Then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("제품 조회가 완료되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
//        ProductDetailResponseDto productDetailResponseDto = (ProductDetailResponseDto) response.getBody().getData();
//        assertEquals(product.getId(), productDetailResponseDto.getId());
//        assertFalse(productDetailResponseDto.isCheckOwner());
//        assertFalse(productDetailResponseDto.isZzimStatus());
//        assertEquals(2, productDetailResponseDto.getZzimCount());
//        assertEquals(imageUrls, productDetailResponseDto.getImageList());
//        assertEquals("owner", productDetailResponseDto.getOwnerNickname());
//        assertEquals("profile", productDetailResponseDto.getProfile());
//        assertEquals(reservationList, productDetailResponseDto.getReservationList());
//
//        // Verify
//        verify(productRepository, times(1)).findById(productId);
//        verify(userService, times(1)).getUserByUserId(Long.toString(product.getUser().getId()));
//        verify(zzimService, times(1)).countByProductId(productId);
//        verify(imageStorageService, times(1)).getImageUrlListByProductId(productId);
//        verify(reservationService, times(1)).getReservationList(null, productId);
//    }
//
//    @Test
//    @DisplayName("제품상세조회-OwnerIsTrue")
//    public void testDetailOwnerIsTrue() {
//        // given
//        Long productId = 1L;
//        Long userId = 1L;
//        User user = User.builder()
//                .id(1L)
//                .kakaoId(123L)
//                .nickname("닉네임")
//                .profile("프로필.jpg")
//                .role(UserRoleEnum.USER)
//                .build();
//
//        ProductRequestDto productRequestDto = ProductRequestDto.builder()
//                .title("제품명")
//                .description("제품 설명")
//                .price(10000)
//                .location("서울")
//                .build();
//
//        UserDetailsImpl userDetails = new UserDetailsImpl(user, "123");
//
//        Product product = new Product(userDetails.getUser(), productRequestDto);
//
//        User owner = User.builder()
//                .nickname("owner")
//                .profile("profile")
//                .build();
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(userService.getUserByUserId(Long.toString(product.getUser().getId()))).thenReturn(owner);
//        when(productService.checkProductOwner(productId, userId)).thenReturn(true);
//        when(zzimService.countByProductId(productId)).thenReturn(2);
//        List<String> imageUrls = Arrays.asList("url1", "url2");
//        when(imageStorageService.getImageUrlListByProductId(productId)).thenReturn(imageUrls);
//        List<ReservationResponseDto> reservationList = new ArrayList<>();
//        when(reservationService.getReservationList(user, productId)).thenReturn(reservationList);
//
//        // When
//        ResponseEntity<ResponseMessage> response = productService.detailProduct(userDetails, productId);
//
//        // Then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("제품 조회가 완료되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
//        ProductDetailResponseDto productDetailResponseDto = (ProductDetailResponseDto) response.getBody().getData();
//        assertEquals(product.getId(), productDetailResponseDto.getId());
//        assertTrue(productDetailResponseDto.isCheckOwner());
//        assertFalse(productDetailResponseDto.isZzimStatus());
//        assertEquals(2, productDetailResponseDto.getZzimCount());
//        assertEquals(imageUrls, productDetailResponseDto.getImageList());
//        assertEquals("owner", productDetailResponseDto.getOwnerNickname());
//        assertEquals("profile", productDetailResponseDto.getProfile());
//        assertEquals(reservationList, productDetailResponseDto.getReservationList());
//
//        // Verify
//        verify(productRepository, times(1)).findById(productId);
//        verify(userService, times(1)).getUserByUserId(Long.toString(product.getUser().getId()));
//        verify(zzimService, times(1)).countByProductId(productId);
//        verify(imageStorageService, times(1)).getImageUrlListByProductId(productId);
//        verify(reservationService, times(1)).getReservationList(user, productId);
//    }
//
//    @Test
//    @DisplayName("제품상세조회-ZzimIsTrue")
//    public void testDetailZzimIsTrue() {
//        // given
//        Long productId = 1L;
//        User user = User.builder()
//                .id(1L)
//                .kakaoId(123L)
//                .nickname("닉네임")
//                .profile("프로필.jpg")
//                .role(UserRoleEnum.USER)
//                .build();
//
//        ProductRequestDto productRequestDto = ProductRequestDto.builder()
//                .title("제품명")
//                .description("제품 설명")
//                .price(10000)
//                .location("서울")
//                .build();
//
//        UserDetailsImpl userDetails = new UserDetailsImpl(user, "123");
//
//        Product product = new Product(userDetails.getUser(), productRequestDto);
//
//        User owner = User.builder()
//                .nickname("owner")
//                .profile("profile")
//                .build();
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(userService.getUserByUserId(Long.toString(product.getUser().getId()))).thenReturn(owner);
//        when(zzimService.getZzimStatus(user, product)).thenReturn(true);
//        when(zzimService.countByProductId(productId)).thenReturn(2);
//        List<String> imageUrls = Arrays.asList("url1", "url2");
//        when(imageStorageService.getImageUrlListByProductId(productId)).thenReturn(imageUrls);
//        List<ReservationResponseDto> reservationList = new ArrayList<>();
//        when(reservationService.getReservationList(user, productId)).thenReturn(reservationList);
//
//        // When
//        ResponseEntity<ResponseMessage> response = productService.detailProduct(userDetails, productId);
//
//        // Then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("제품 조회가 완료되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
//        ProductDetailResponseDto productDetailResponseDto = (ProductDetailResponseDto) response.getBody().getData();
//        assertEquals(product.getId(), productDetailResponseDto.getId());
//        assertFalse(productDetailResponseDto.isCheckOwner());
//        assertTrue(productDetailResponseDto.isZzimStatus());
//        assertEquals(2, productDetailResponseDto.getZzimCount());
//        assertEquals(imageUrls, productDetailResponseDto.getImageList());
//        assertEquals("owner", productDetailResponseDto.getOwnerNickname());
//        assertEquals("profile", productDetailResponseDto.getProfile());
//        assertEquals(reservationList, productDetailResponseDto.getReservationList());
//
//        // Verify
//        verify(productRepository, times(1)).findById(productId);
//        verify(userService, times(1)).getUserByUserId(Long.toString(product.getUser().getId()));
//        verify(zzimService, times(1)).countByProductId(productId);
//        verify(imageStorageService, times(1)).getImageUrlListByProductId(productId);
//        verify(reservationService, times(1)).getReservationList(user, productId);
//    }
//
//    @Test
//    @DisplayName("제품검색-정상케이스")
//    public void testSearchProductList() {
//        // given
//        User user = new User();
//        Product product = new Product();
//        UserDetailsImpl userDetails = new UserDetailsImpl(user, "123");
//        List<Product> productList = Arrays.asList(product);
//        String primeImageUrl = "https://example.com/image.png";
//
//        when(searchQueryRepository.searchProduct(eq(user), anyString(), anyString(), anyLong(), anyInt())).thenReturn(productList);
//        when(imageStorageService.getImageUrlListByProductId(any())).thenReturn(Arrays.asList(primeImageUrl));
//        when(reservationService.reservationCount()).thenReturn(Arrays.asList());
//        when(zzimService.getZzimStatus(eq(user), eq(product))).thenReturn(false);
//
//        // when
//        ResponseEntity<ResponseMessage> response = productService.searchProductList(userDetails, "title", "location",123L,2);
//
//        // then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("검색 조회가 되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
//
//        // verify
//        verify(searchQueryRepository, times(1)).searchProduct(eq(user), anyString(), anyString(), anyLong(), anyInt());
//        verify(imageStorageService, times(1)).getImageUrlListByProductId(any());
//        verify(reservationService, times(1)).reservationCount();
//        verify(zzimService, times(1)).getZzimStatus(eq(user), eq(product));
//    }
//
//    @Test
//    @DisplayName("제품검색-UserIsNull")
//    void testSearchProductListUserIsNull() {
//        // given
//        Product product = new Product();
//        List<Product> productList = Arrays.asList(product);
//        String primeImageUrl = "https://example.com/image.png";
//
//        when(searchQueryRepository.searchProduct(isNull(), anyString(), anyString(), anyLong(), anyInt())).thenReturn(productList);
//        when(imageStorageService.getImageUrlListByProductId(any())).thenReturn(Arrays.asList(primeImageUrl));
//        when(reservationService.reservationCount()).thenReturn(Arrays.asList());
//        when(zzimService.getZzimStatus(isNull(), eq(product))).thenReturn(false);
//
//        // when
//        ResponseEntity<ResponseMessage> response = productService.searchProductList(null, "title", "location",123L,2);
//
//        // then
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("검색 조회가 되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
//
//        // verify
//        verify(searchQueryRepository, times(1)).searchProduct(isNull(), anyString(), anyString(), anyLong(), anyInt());
//        verify(imageStorageService, times(1)).getImageUrlListByProductId(any());
//        verify(reservationService, times(1)).reservationCount();
//        verify(zzimService, times(1)).getZzimStatus(isNull(), eq(product));
//    }
//
//    @Test
//    @DisplayName("인기제품-True")
//    void testGetMostProductTrue() {
//        // Given
//        Product product = Product.builder()
//                .id(1L)
//                .title("제목")
//                .description("내용")
//                .price(15000)
//                .location("서울 강남구")
//                .zzimCount(3)
//                .build();
//
//        ReservationCountResponseDto countResponseDto = new ReservationCountResponseDto(1L, 5L);
//        List<ReservationCountResponseDto> reservationCounts = Collections.singletonList(countResponseDto);
//
//        when(reservationService.reservationCount()).thenReturn(reservationCounts);
//        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
//
//        // When
//        boolean isMostProduct = productService.isMostProduct(product);
//
//        // Then
//        assertTrue(isMostProduct);
//
//        // Verify
//        verify(reservationService).reservationCount();
//        verify(productRepository).findById(countResponseDto.getProductId());
//    }
//
//    @Test
//    @DisplayName("인기제품-False")
//    void testGetMostProductFalse() {
//        // Given
//
//        Product product = Product.builder()
//                .id(1L)
//                .title("제목")
//                .description("내용")
//                .price(15000)
//                .location("서울 강남구")
//                .zzimCount(2)
//                .build();
//
//        List<ReservationCountResponseDto> reservationCountList = Arrays.asList(
//                new ReservationCountResponseDto(2L, 10L),
//                new ReservationCountResponseDto(3L, 5L),
//                new ReservationCountResponseDto(4L, 1L)
//        );
//
//        // when
//        when(reservationService.reservationCount()).thenReturn(reservationCountList);
//        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        // then
//        boolean result = productService.isMostProduct(product);
//
//        // verify
//        assertFalse(result);
//        verify(reservationService).reservationCount();
//        verify(productRepository, times(1)).findById(anyLong());
//    }
//
////    @Test
////    @DisplayName("전체조회-정상")
////    public void testGetMainPage() {
////        // given
////        User user = User.builder()
////                .id(1L)
////                .kakaoId(123L)
////                .nickname("닉네임")
////                .profile("프로필.jpg")
////                .role(UserRoleEnum.USER)
////                .build();
////        when(userDetails.getUser()).thenReturn(user);
////
////        List<AcceptReservationResponseDto> dealList = new ArrayList<>();
////        when(reservationService.getAcceptedReservationList()).thenReturn(dealList);
////
////        List<Product> productList1 = new ArrayList<>();
////        List<Product> productList2 = new ArrayList<>();
////        when(productRepository.findRandomProduct(8)).thenReturn(productList1);
////        when(productRepository.findRandomProduct(6)).thenReturn(productList2);
////        when(zzimService.getZzimCount(user)).thenReturn(0);
////
////        // when
////        ResponseEntity<ResponseMessage> response = productService.getMainPage(userDetails);
////
////        // then
////        assertEquals(HttpStatus.OK, response.getStatusCode());
////        assertEquals("메인페이지 조회되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
////
////        // verify
////        verify(reservationService, times(1)).getAcceptedReservationList();
////        verify(productRepository, times(1)).findRandomProduct(8);
////        verify(productRepository, times(1)).findRandomProduct(6);
////        verify(imageStorageService, times(productList1.size() + productList2.size())).getImageUrlListByProductId(anyLong());
////        verify(zzimService, times(productList1.size() + productList2.size())).getZzimStatus(any(User.class), any(Product.class));
////        verify(zzimService, times(1)).getZzimCount(user);
////    }
//
////    @Test
////    @DisplayName("전체조회-UserIsNull")
////    public void testGetMainPageUserIsNull() {
////        // given
////        UserDetailsImpl userDetails = null;
////
////        List<AcceptReservationResponseDto> dealList = new ArrayList<>();
////        when(reservationService.getAcceptedReservationList()).thenReturn(dealList);
////
////        List<Product> productList1 = new ArrayList<>();
////        List<Product> productList2 = new ArrayList<>();
////        when(productRepository.findRandomProduct(8)).thenReturn(productList1);
////        when(productRepository.findRandomProduct(6)).thenReturn(productList2);
////        when(zzimService.getZzimCount(null)).thenReturn(0);
////
////        // when
////        ResponseEntity<ResponseMessage> response = productService.getMainPage(userDetails);
////
////        // then
////        assertEquals(HttpStatus.OK, response.getStatusCode());
////        assertEquals("메인페이지 조회되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());
////
////        // verify
////        verify(reservationService, times(1)).getAcceptedReservationList();
////        verify(productRepository, times(1)).findRandomProduct(8);
////        verify(productRepository, times(1)).findRandomProduct(6);
////        verify(imageStorageService, times(productList1.size() + productList2.size())).getImageUrlListByProductId(anyLong());
////        verify(zzimService, times(productList1.size() + productList2.size())).getZzimStatus(any(User.class), any(Product.class));
////        verify(zzimService, times(1)).getZzimCount(null);
////    }
//
//    @Test
//    @DisplayName("상품 ID로 상품 조회 성공")
//    void testFindProductByIdSuccess() {
//// Given
//        Long id = 1L;
//        Product product = Product.builder()
//                .id(id)
//                .title("제목")
//                .description("내용")
//                .price(15000)
//                .location("서울 강남구")
//                .zzimCount(2)
//                .build();
//
//        when(productRepository.findById(id)).thenReturn(Optional.of(product));
//
//        // When
//        Product foundProduct = productService.findProductById(id);
//
//        // Then
//        assertAll(
//                () -> assertNotNull(foundProduct),
//                () -> assertEquals(product.getId(), foundProduct.getId()),
//                () -> assertEquals(product.getTitle(), foundProduct.getTitle()),
//                () -> assertEquals(product.getDescription(), foundProduct.getDescription()),
//                () -> assertEquals(product.getPrice(), foundProduct.getPrice()),
//                () -> assertEquals(product.getLocation(), foundProduct.getLocation()),
//                () -> assertEquals(product.getZzimCount(), foundProduct.getZzimCount())
//        );
//
//        verify(productRepository, times(1)).findById(id);
//    }
//
//    @Test
//    @DisplayName("상품 ID로 상품 조회 실패")
//    void testFindProductByIdFailure() {
//        // Given
//        Long id = 1L;
//        when(productRepository.findById(id)).thenReturn(Optional.empty());
//
//        // When, Then
//        CustomException expectedException = assertThrows(
//                CustomException.class,
//                () -> productService.findProductById(id),
//                "CustomException이 발생할 것으로 예상했지만 throw되지 않았습니다."
//        );
//
//        //then
//        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, expectedException.getErrorCode());
//    }
//}
//
