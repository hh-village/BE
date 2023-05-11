package com.sparta.village;

import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.product.dto.AcceptReservationResponseDto;
import com.sparta.village.domain.product.dto.MainResponseDto;
import com.sparta.village.domain.product.dto.ProductResponseDto;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.product.service.ProductService;
import com.sparta.village.domain.reservation.repository.ReservationRepository;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.repository.UserRepository;
import com.sparta.village.domain.visitor.repository.VisitorCountRepository;
import com.sparta.village.domain.zzim.repository.ZzimRepository;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@SpringBootTest
@Transactional
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class MainPageTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ZzimRepository zzimRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private VisitorCountRepository visitorCountRepository;

//    @BeforeEach
//    void setUp() {
//        User user1 = User.builder()
//                .id(1L)
//                .nickname("닉네임")
//                .kakaoId(123L)
//                .profile("프로필")
//                .role(UserRoleEnum.USER)
//                .isDeleted(false)
//                .build();
//        User user2 = User.builder()
//                .id(2L)
//                .nickname("닉네임2")
//                .kakaoId(124L)
//                .profile("프로필2")
//                .role(UserRoleEnum.USER)
//                .isDeleted(false)
//                .build();
//        User user3 = User.builder()
//                .id(3L)
//                .nickname("닉네임3")
//                .kakaoId(125L)
//                .profile("프로필3")
//                .role(UserRoleEnum.USER)
//                .isDeleted(false)
//                .build();
//        List<User> savedUsers = userRepository.saveAll(Arrays.asList(user1, user2, user3));
//        System.out.println("유저레포지토리 : " + userRepository.findById(1L).get().getId());
//
////        List<MultipartFile> imageFile = new ArrayList<>();
////        MultipartFile dummyImage1 = new MockMultipartFile("image1", "image1.jpg", "image/jpeg", new byte[10]);
////        MultipartFile dummyImage2 = new MockMultipartFile("image2", "image2.jpg", "image/jpeg", new byte[10]);
////        imageFile.add(dummyImage1);
////        imageFile.add(dummyImage2);
//
//        Product product1 = Product.builder().id(1L).title("상품제목1").description("상품내용1").price(15000).location("서울 강남구1").user(user1).zzimCount(3).isDeleted(false).build();
//        Product product2 = Product.builder().id(2L).title("상품제목2").description("상품내용2").price(16000).location("서울 강남구2").user(user2).zzimCount(1).isDeleted(false).build();
//        Product product3 = Product.builder().id(3L).title("상품제목3").description("상품내용3").price(17000).location("서울 강남구3").user(user3).zzimCount(0).isDeleted(false).build();
//
//        productRepository.saveAll(Arrays.asList(product1, product2, product3));
//
//        Reservation reservation1 = Reservation.builder()
//                .id(1L)
//                .status("returned")
//                .startDate(LocalDate.of(2023, 4, 23))
//                .endDate(LocalDate.of(2023, 4, 23))
//                .user(user3)
//                .product(product1)
//                .isDeleted(false)
//                .build();
//
//        Reservation reservation2 = Reservation.builder()
//                .id(2L)
//                .status("returned")
//                .startDate(LocalDate.of(2023, 4, 24))
//                .endDate(LocalDate.of(2023, 4, 24))
//                .user(user2)
//                .product(product1)
//                .isDeleted(false)
//                .build();
//
//        Reservation reservation3 = Reservation.builder()
//                .id(3L)
//                .status("returned")
//                .startDate(LocalDate.of(2023, 4, 20))
//                .endDate(LocalDate.of(2023, 4, 20))
//                .user(user3)
//                .product(product1)
//                .isDeleted(false)
//                .build();
//
//        Reservation reservation4 = Reservation.builder()
//                .id(4L)
//                .status("accepted")
//                .startDate(LocalDate.of(2023, 4, 20))
//                .endDate(LocalDate.of(2023, 4, 20))
//                .user(user3)
//                .product(product2)
//                .isDeleted(false)
//                .build();
//
//        Reservation reservation5 = Reservation.builder()
//                .id(5L)
//                .status("accepted")
//                .startDate(LocalDate.of(2023, 4, 20))
//                .endDate(LocalDate.of(2023, 4, 20))
//                .user(user1)
//                .product(product3)
//                .isDeleted(false)
//                .build();
//        Reservation reservation6 = Reservation.builder()
//                .id(6L)
//                .status("accepted")
//                .startDate(LocalDate.of(2023, 4, 11))
//                .endDate(LocalDate.of(2023, 4, 11))
//                .user(user2)
//                .product(product1)
//                .isDeleted(false)
//                .build();
//        Reservation reservation7 = Reservation.builder()
//                .id(7L)
//                .status("returned")
//                .startDate(LocalDate.of(2023, 4, 11))
//                .endDate(LocalDate.of(2023, 4, 11))
//                .user(user1)
//                .product(product2)
//                .isDeleted(false)
//                .build();
//        Reservation reservation8 = Reservation.builder()
//                .id(8L)
//                .status("returned")
//                .startDate(LocalDate.of(2023, 4, 11))
//                .endDate(LocalDate.of(2023, 4, 11))
//                .user(user2)
//                .product(product3)
//                .isDeleted(false)
//                .build();
//
//        reservationRepository.saveAll(Arrays.asList(reservation1, reservation2, reservation3, reservation4, reservation5, reservation6, reservation7, reservation8));
//
//        Zzim zzim1 = new Zzim(user1, product1);
//        Zzim zzim2 = new Zzim(user2, product1);
//        Zzim zzim3 = new Zzim(user3, product1);
//        Zzim zzim4 = new Zzim(user1, product2);
//        zzimRepository.saveAll(Arrays.asList(zzim1, zzim2, zzim3, zzim4));
//
//        Image image1 = Image.builder()
//                .id(1L)
//                .imageUrl("imageUrl1")
//                .product(product1)
//                .isDeleted(false)
//                .build();
//
//        Image image2 = Image.builder()
//                .id(1L)
//                .imageUrl("imageUrl1")
//                .product(product2)
//                .isDeleted(false)
//                .build();
//
//        Image image3 = Image.builder()
//                .id(1L)
//                .imageUrl("imageUrl1")
//                .product(product3)
//                .isDeleted(false)
//                .build();
//        imageRepository.saveAll(Arrays.asList(image1, image2, image3));
//
//        visitorCountRepository.save(new Visitor(50));
//    }

    @Transactional
    @Test
    void getMainPage() {

        User user = userRepository.findById(1L).orElseThrow(() -> new RuntimeException("User not found"));

        UserDetailsImpl userDetails = new UserDetailsImpl(user, "123");

        ResponseEntity<ResponseMessage> response = productService.getMainPage(userDetails);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("메인페이지 조회되었습니다.", Objects.requireNonNull(response.getBody()).getMessage());

        MainResponseDto mainResponseDto = (MainResponseDto) response.getBody().getData();
        assertNotNull(mainResponseDto);

        List<AcceptReservationResponseDto> dealList = mainResponseDto.getDealList();
        List<ProductResponseDto> randomProductList = mainResponseDto.getRandomProduct();
        List<ProductResponseDto> latestProductList = mainResponseDto.getLatestProduct();
        int zzimCount = mainResponseDto.getZzimCount();
        int visitorCount = mainResponseDto.getVisitorCount();

        assertEquals(2, dealList.size());
        assertEquals(4, dealList.get(0).getId());
        assertEquals("닉네임2", dealList.get(0).getCustomerNickname());
        assertEquals("닉네임3", dealList.get(0).getOwnerNickname());
        assertEquals(5, dealList.get(1).getId());
        assertEquals("닉네임3", dealList.get(1).getCustomerNickname());
        assertEquals("닉네임1", dealList.get(1).getOwnerNickname());


        assertEquals(4, randomProductList.size());
        assertEquals(4, randomProductList.get(0).getId());
        assertEquals("상품제목4", randomProductList.get(0).getTitle());
        assertEquals("image4", randomProductList.get(0).getImage());
        assertEquals("서울 강남구4", randomProductList.get(0).getLocation());
        assertEquals(1800, randomProductList.get(0).getPrice());
        assertFalse(randomProductList.get(0).isHot());
        assertFalse(randomProductList.get(0).isCheckZzim());

        assertEquals(4, latestProductList.size());
        assertEquals(4, randomProductList.get(0).getId());
        assertEquals("상품제목4", randomProductList.get(0).getTitle());
        assertEquals("image4", randomProductList.get(0).getImage());
        assertEquals("서울 강남구4", randomProductList.get(0).getLocation());
        assertEquals(1800, randomProductList.get(0).getPrice());
        assertFalse(randomProductList.get(0).isHot());
        assertFalse(randomProductList.get(0).isCheckZzim());

        assertEquals(2, zzimCount);
        assertEquals(50, visitorCount);
    }

}