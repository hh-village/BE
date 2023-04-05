package com.sparta.village.domain.reservation.service;

import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.product.service.ProductService;
import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import com.sparta.village.domain.reservation.dto.ReservationResponseDto;
import com.sparta.village.domain.reservation.dto.StatusRequestDto;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.reservation.repository.ReservationRepository;
import com.sparta.village.domain.user.service.KakaoUserService;
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
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final KakaoUserService userService;

    @Transactional
    public ResponseEntity<ResponseMessage> reserve(Long productId, ReservationRequestDto requestDto, Long userId) {
        //제품 있는지 체크(제품 등록 코드 완성되면 추가하기!!)
        if (!productRepository.existsById(productId)) {
            throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        //예약 가능한 날짜인지 체크
        if (reservationRepository.checkOverlapDateByProductId(productId, requestDto.getStartDate(), requestDto.getEndDate())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESERVATION_DATE);
        }
        //예약 테이블에 저장
        reservationRepository.saveAndFlush(new Reservation(productId, userId, requestDto));
        return ResponseMessage.SuccessResponse("예약 되었습니다.", "");
    }

    @Transactional
    public ResponseEntity<ResponseMessage> deleteReservation(Long id, Long userId) {
        checkReservationId(id);
        checkReservationUserId(userId);
        reservationRepository.deleteById(id);
        return ResponseMessage.SuccessResponse("예약 취소되었습니다.", "");
    }

    @Transactional
    public ResponseEntity<ResponseMessage> changeStatus(Long id, StatusRequestDto requestDto, Long userId) {
        checkReservationId(id);
//        productService.checkProductOwner(reservationRepository.findProductIdById(id), userId);
        reservationRepository.updateStatus(id, requestDto.getStatus());
        return ResponseMessage.SuccessResponse("상태 변경되었습니다.", "");
    }


    private void checkReservationId(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
    }

    private void checkReservationUserId(Long userId) {
        if (!reservationRepository.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.NOT_AUTHOR);
        }
    }

    public List<ReservationResponseDto> getReservationList() {
        List<ReservationResponseDto> reservationList = reservationRepository.findAllReservationDto();
        reservationList.forEach(r -> r.setNickname(userService.getNicknameByUserId(r.getNickname())));
        return reservationList;
    }





}
