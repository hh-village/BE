package com.sparta.village.domain.user.service;

import com.sparta.village.domain.image.service.ImageStorageService;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.repository.ReservationRepository;
import com.sparta.village.domain.user.dto.*;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.repository.UserRepository;
import com.sparta.village.domain.zzim.repository.ZzimRepository;
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
public class UserService {
    private final UserRepository userRepository;
    private final ZzimRepository zzimRepository;
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;
    private final ImageStorageService imageStorageService;

    @Transactional
    public ResponseEntity<ResponseMessage> updateNickname(String newNickname, User user) {
        if (newNickname == null || newNickname.strip().equals("")) {
            throw new CustomException(ErrorCode.BAD_NICKNAME);
        }
        if (userRepository.findByNickname(newNickname).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        user.updateNickname(newNickname);
        userRepository.save(user);
        return ResponseMessage.SuccessResponse("변경 완료되었습니다.",new UserResponseDto(user.getProfile(), user.getNickname()));
    }

    @Transactional
    public ResponseEntity<ResponseMessage> getUserItemList(User user, String key) {
        List<MyProductsResponseDto> myProductList = productRepository.findAllByUser(user).stream()
                .map(product -> new MyProductsResponseDto(user, product, imageStorageService.getFirstImageUrlByProductId(product.getId()))).toList();
        List<MyReservationsResponseDto> myReservationList = reservationRepository.findByUser(user).stream()
                .map(reservation -> new MyReservationsResponseDto(user, reservation, imageStorageService.getFirstImageUrlByProductId(reservation.getProduct().getId()))).toList();
        List<ZzimsResponseDto> myZzimList = zzimRepository.findByUser(user).stream()
                .map(zzim -> new ZzimsResponseDto(user, zzim, imageStorageService.getFirstImageUrlByProductId(zzim.getProduct().getId()))).toList();
        List<?> productList = "products".equals(key) ?  myProductList :
                                "rents".equals(key) ?  myReservationList :
                                "zzims".equals(key) ?  myZzimList :
                                null;
        if(productList == null) {
            throw new CustomException(ErrorCode.BAD_PARAMETER);
        }
        return ResponseMessage.SuccessResponse("조회가 완료되었습니다.", new MyPageResponseDto(user, user.getProfile(), productList));

    }


    public User getUserByUserId(String userId) {
        return userRepository.findById(Long.parseLong(userId)).orElse(null);
    }

    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public void updateProfileIfNeeded(User user, String profile) {
        if (!user.getProfile().equals(profile)) {
            user.updateProfile(profile);
            userRepository.saveAndFlush(user);
        }
    }

}










