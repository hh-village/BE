package com.sparta.village.domain.product.service;

import com.sparta.village.domain.chat.service.ChatService;
import com.sparta.village.domain.image.service.ImageStorageService;
import com.sparta.village.domain.product.dto.*;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.product.repository.SearchQueryRepository;
import com.sparta.village.domain.product.dto.AcceptReservationResponseDto;
import com.sparta.village.domain.reservation.dto.ReservationCountResponseDto;
import com.sparta.village.domain.reservation.service.ReservationService;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.service.UserService;
import com.sparta.village.domain.visitor.repository.VisitorCountRepository;
import com.sparta.village.domain.zzim.service.ZzimService;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final VisitorCountRepository visitorCountRepository;
    private final SearchQueryRepository searchQueryRepository;
    private final UserService userService;
    private final ZzimService zzimService;
    private final ChatService chatRoomService;
    private final ReservationService reservationService;
    private final ImageStorageService imageStorageService;
    private final RedisTemplate<String, Integer> redisTemplate;

    @Transactional
    public ResponseEntity<ResponseMessage> getMainPage(UserDetailsImpl userDetails) {
//        double beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
        redisTemplate.opsForValue().increment("visitor_count",1);
        Long userId = userDetails == null ? null : userDetails.getUser().getId();

        Object[] randomPopularProduct = productRepository.getOnePopularProduct(userId).get(0);
        Long randomPopularProductId = randomPopularProduct == null ? null : Long.parseLong(randomPopularProduct[0].toString());

        List<AcceptReservationResponseDto> dealList = productRepository.getDealList();
        List<ProductResponseDto> randomProduct = new ArrayList<>(productRepository.findRandomEightProduct(userId, randomPopularProductId).stream().map(p ->
                new ProductResponseDto(Long.parseLong(p[0].toString()), (String) p[1], (String) p[2], (String) p[3], Integer.parseInt(String.valueOf(p[4])), Integer.parseInt(String.valueOf(p[5])) == 1, Integer.parseInt(String.valueOf(p[6])) == 1)).toList());
        List<ProductResponseDto> latestProduct = productRepository.findLatestSixProduct(userId).stream().map(p ->
                new ProductResponseDto(Long.parseLong(p[0].toString()), (String)p[1], (String)p[2], (String)p[3], Integer.parseInt(String.valueOf(p[4])), Integer.parseInt(String.valueOf(p[5])) == 1, Integer.parseInt(String.valueOf(p[6])) == 1)).toList();

        if (randomPopularProduct != null) {
            int randomIndex = (int) (Math.random() * (8));
            randomProduct.set(randomIndex, new ProductResponseDto(randomPopularProductId, (String)randomPopularProduct[1], (String)randomPopularProduct[2], (String)randomPopularProduct[3], Integer.parseInt(String.valueOf(randomPopularProduct[4])), true, Integer.parseInt(String.valueOf(randomPopularProduct[5]))==1));
        }
//        double afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
//        double secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
//        System.out.println("시간차이(m) : "+secDiffTime);
        return ResponseMessage.SuccessResponse("메인페이지 조회되었습니다.", new MainResponseDto(dealList, randomProduct, zzimService.getZzimCount(userId), latestProduct, visitorCountRepository.findVisitorCountById()));
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

        productRepository.deleteAllAboutProductById(id);

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
        int ownerReturned = reservationService.getReservationCountByUser(owner, "returned");
        int ownerAccepted = reservationService.getReservationCountByUser(owner, "accepted");
        int ownerWaiting = reservationService.getReservationCountByUser(owner, "waiting");
        ProductDetailResponseDto productDetailResponseDto = new ProductDetailResponseDto(product, checkOwner, zzimStatus,
                zzimService.countByProductId(id), imageStorageService.getImageUrlListByProductId(id),
                owner.getNickname(), owner.getProfile(), ownerReturned, ownerAccepted, ownerWaiting, reservationService.getReservationList(user, id));

        return ResponseMessage.SuccessResponse("제품 조회가 완료되었습니다.", productDetailResponseDto);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ResponseMessage> searchProductList(UserDetailsImpl userDetails, String title, String location, Long lastId, int size) {
        Long userId = userDetails == null ? null : userDetails.getUser().getId();
        List<ProductResponseDto> productList = searchQueryRepository.searchProduct(userId, title, location, lastId, size);
        return ResponseMessage.SuccessResponse("검색 조회가 되었습니다.", new SearchResponseDto(productList, productList.size() < size));
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