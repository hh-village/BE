package com.sparta.village.domain.reservation.service;

import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.product.service.ProductService;
import com.sparta.village.domain.reservation.dto.AcceptReservationResponseDto;
import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import com.sparta.village.domain.reservation.dto.ReservationResponseDto;


import com.sparta.village.domain.reservation.dto.StatusRequestDto;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.reservation.repository.ReservationRepository;
import com.sparta.village.domain.user.entity.User;
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
    public ResponseEntity<ResponseMessage> reserve(Long productId, ReservationRequestDto requestDto, User user) {
        //제품 있는지 체크(제품 등록 코드 완성되면 추가하기!!)

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );

        //예약 가능한 날짜인지 체크
        if (reservationRepository.checkOverlapDateByProductId(product, requestDto.getStartDate(), requestDto.getEndDate())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESERVATION_DATE);
        }
        //예약 테이블에 저장
        reservationRepository.saveAndFlush(new Reservation(product, user, requestDto));
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
        return reservationRepository.findAll().stream()
                .map(r -> new ReservationResponseDto(r.getId(), r.getStartDate(), r.getEndDate(), r.getStatus(), r.getUser().getNickname())).toList();
    }

    public ResponseEntity<ResponseMessage> getAcceptedReservationList() {
        List<AcceptReservationResponseDto> acceptReservationList = reservationRepository.findByStatus("accepted").stream()
                .map(r -> new AcceptReservationResponseDto(r.getId(), r.getUser().getNickname(), r.getProduct().getUser().getNickname())).toList();
//        List<AcceptReservationResponseDto> acceptReservationList = reservationRepository.findAcceptedReservationDto();
//        acceptReservationList.forEach(r -> r.setOwnerNickname(userService.getUserByUserId(r.getOwnerNickname()).getNickname()));
//        acceptReservationList.forEach(r -> r.setCustomerNickname(userService.getUserByUserId(r.getCustomerNickname()).getNickname()));
        return ResponseMessage.SuccessResponse("검색완료 되었습니다.", acceptReservationList);
    }
}
