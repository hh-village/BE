package com.sparta.village.domain.reservation.service;

import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import com.sparta.village.domain.reservation.dto.StatusRequestDto;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.reservation.repository.ReservationRepository;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;

    @Transactional
    public ResponseEntity<ResponseMessage> reserve(Long productId, ReservationRequestDto requestDto, Long userId) {
        //제품 있는지 체크(제품 등록 코드 완성되면 추가하기!!)

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
        if (!reservationRepository.existsById(id)) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        if (!reservationRepository.existsByUserId(userId)) {
            throw new CustomException(ErrorCode.NOT_AUTHOR);
        }
        reservationRepository.deleteById(id);
        return ResponseMessage.SuccessResponse("예약 취소되었습니다.", "");
    }
}
