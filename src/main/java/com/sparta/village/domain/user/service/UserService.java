package com.sparta.village.domain.user.service;

import com.sparta.village.domain.image.service.ImageStorageService;
import com.sparta.village.domain.product.dto.AcceptReservationResponseDto;
import com.sparta.village.domain.product.dto.MainResponseDto;
import com.sparta.village.domain.product.dto.ProductResponseDto;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.reservation.repository.ReservationRepository;
import com.sparta.village.domain.user.dto.*;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.repository.UserRepository;
import com.sparta.village.domain.zzim.entity.Zzim;
import com.sparta.village.domain.zzim.repository.ZzimRepository;
import com.sparta.village.domain.zzim.service.ZzimService;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

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
        double beforeTime = System.currentTimeMillis();
        List<?> productList = key.equals("products") ? userRepository.findProductsByUserIdAndKey(user.getId(), key).stream().map(p ->
                                                       new MyProductsResponseDto(Long.parseLong(p[1].toString()),
                                                                                (String) p[2], (String) p[3],
                                                                                 new SimpleDateFormat("yyyy-MM-dd").format(p[4]) )).toList() :
                              key.equals("rents")    ? userRepository.findReservationsByUserIdAndKey(user.getId(), key).stream().map(r ->
                                                       new MyReservationsResponseDto(Long.parseLong(r[1].toString()),
                                                                                     Long.parseLong(r[2].toString()),
                                                                                     r[3].toString(), r[4].toString(),
                                                                                    ((java.sql.Date) r[5]).toLocalDate(),
                                                                                    ((java.sql.Date) r[6]).toLocalDate(),
                                                                                     r[7].toString())).toList():
                              key.equals("zzims")    ? userRepository.findZzimsByUserIdAndKey(user.getId(), key).stream().map(z ->
                                                       new ZzimsResponseDto(Long.parseLong(z[1].toString()),
                                                                            z[3].toString(),
                                                                            z[4].toString())).toList():
                              null;

        if(productList == null) {
            throw new CustomException(ErrorCode.BAD_PARAMETER);
        }
        double afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        double secDiffTime = (afterTime - beforeTime)/1000; //두 시간에 차 계산
        System.out.println("시간차이(m) : "+secDiffTime);
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










