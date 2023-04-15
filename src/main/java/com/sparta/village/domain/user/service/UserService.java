package com.sparta.village.domain.user.service;

import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.image.service.ImageStorageService;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.dto.UserLevelDto;
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

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service // spring service 등록
@RequiredArgsConstructor // final @NonNull 필드를 사용하여 생성자를 자동으로 생성
// User Service 클래스 선언
public class UserService {
    private final UserRepository userRepository;
    private final ZzimRepository zzimRepository;
    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;
    private final ReservationRepository reservationRepository;
    private final ImageStorageService imageStorageService;

    @Transactional
    // 사용자의 닉네임을 업데이트하고 업데이트된 사용자를 저장하는 메소드
    public ResponseEntity<ResponseMessage> updateNickname(String newNickname, User user) {

        Optional<User> existingUser = userRepository.findByNickname(newNickname);
        if (existingUser.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 사용자 객체의 닉네임을 새 닉네임으로 설정
        user.setNickname(newNickname);
        // userRepository에 변경된 사용자 정보를 저장
        userRepository.save(user);
        // 변경이 완료되었음을 알리는 응답 메시지 생성
        return ResponseMessage.SuccessResponse("변경 완료되었습니다.",new UserResponseDto(user.getProfile(), user.getNickname()));
    }


    public ResponseEntity<ResponseMessage> getUserItemList(User user, String key) {
        return ResponseMessage.SuccessResponse("조회가 완료되었습니다.", new MyPageResponseDto(user, getUserProfile(user), setUserItemList(user, key)));

    }

    private List<?> setUserItemList(User user, String key) {
        List<?> productList = "products".equals(key) ? productRepository.findAllByUser(user).stream()
                                        .map(product -> new MyProductsResponseDto(user, product, imageStorageService.getFirstImageUrlByProductId(product.getId())))
                                        .toList() :
                                 "rents".equals(key) ? reservationRepository.findByUser(user).stream()
                                        .map(reservation -> new MyReservationsResponseDto(user, reservation, imageStorageService.getFirstImageUrlByProductId(reservation.getProduct().getId())))
                                        .toList() :
                                 "zzims".equals(key) ? zzimRepository.findByUser(user).stream()
                                        .map(zzim -> new ZzimsResponseDto(user, zzim, imageStorageService.getFirstImageUrlByProductId(zzim.getProduct().getId())))
                                        .toList() :
                                        null;
        if(productList == null) {
            throw new CustomException(ErrorCode.BAD_PARAMETER);
        }
        return productList;
    }


    public String getUserProfile(User user) {
        List<UserLevelDto> userLevelDtoList = reservationRepository.findUserLevelData(user);
        long count = userLevelDtoList.size();
        long totalDate = 0L;
        for (UserLevelDto userLevelDto : userLevelDtoList) {
            totalDate += Duration.between(userLevelDto.getStartDate().atStartOfDay(), userLevelDto.getEndDate().atStartOfDay()).toDays();
        }
        String profile = (totalDate > 81 && count > 8) ? "profile5" :
                (totalDate > 27 && count > 6) ? "profile4" :
                (totalDate > 9 && count > 4) ? "profile3" :
                (totalDate > 3 && count > 2) ? "profile2" :
                "profile1";
        user.setProfile(profile);
        userRepository.save(user);
        return profile;
    }

    public User getUserByUserId(String userId) {
        return userRepository.findById(Long.parseLong(userId)).orElse(null);
    }

    public User getUserByNickname(String nickname) {
        return userRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

}










