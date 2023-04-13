package com.sparta.village.domain.user.service;


import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.reservation.repository.ReservationRepository;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.repository.UserRepository;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // spring service 등록
@RequiredArgsConstructor // final @NonNull 필드를 사용하여 생성자를 자동으로 생성
// User Service 클래스 선언
public class UserService {

        private final UserRepository userRepository;
        private final ProductRepository productRepository;
        private final ReservationRepository reservationRepository;


    // 사용자의 닉네임을 업데이트하고 업데이트된 사용자를 저장하는 메소드
    public ResponseEntity<ResponseMessage> updateNickname(String newNickname, User user) {

        Optional<User> existingUser = userRepository.findByNickname(newNickname);
        if (existingUser.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }

        // 사용자 객체의 닉네임을 새 닉네임으로 설정
        user.setNickname(newNickname);
        // userRepository에 변경된 사용자 정보를 저장

        // 변경이 완료되었음을 알리는 응답 메시지 생성
        return ResponseMessage.SuccessResponse("변경 완료되었습니다.","");
    }

    public ResponseEntity<ResponseMessage> getUsersItems(User user, String key) {
        List<Product> myProductList = productRepository.findAllByUser(user);

        List<Product> myReservationProductList = reservationRepository.findByUser(user)
                .stream()
                .map(Reservation::getProduct)
                .toList();

    }
}










