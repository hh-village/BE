package com.sparta.village.domain.reservation.service;

import com.sparta.village.domain.image.service.ImageStorageService;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.dto.*;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.reservation.repository.ReservationRepository;
import com.sparta.village.domain.user.entity.User;
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
    private final ImageStorageService imageStorageService;

    @Transactional
    public ResponseEntity<ResponseMessage> reserve(Long productId, ReservationRequestDto requestDto, User user) {
        //제품 있는지 체크(제품 등록 코드 완성되면 추가하기!!)
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );

        //예약 가능한 날짜인지 체크
        if (reservationRepository.checkOverlapDateByProduct(product, requestDto.getStartDate(), requestDto.getEndDate())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESERVATION_DATE);
        }
        //예약 테이블에 저장
        reservationRepository.saveAndFlush(new Reservation(product, user, requestDto));
        return ResponseMessage.SuccessResponse("예약 되었습니다.", "");
    }

    @Transactional
    public ResponseEntity<ResponseMessage> deleteReservation(Long id, User user) {
        Reservation reservation = findReservationById(id);
        if (checkReservationOwner(reservation, user)) {
            throw new CustomException(ErrorCode.NOT_AUTHOR);
        }
        reservationRepository.deleteById(id);
        return ResponseMessage.SuccessResponse("예약 취소되었습니다.", "");
    }

    @Transactional
    public ResponseEntity<ResponseMessage> changeStatus(Long id, StatusRequestDto requestDto, Long userId) {
        Reservation reservation = findReservationById(id);
//        productService.checkProductOwner(reservationRepository.findProductIdById(id), userId);
        reservationRepository.updateStatus(reservation.getId(), requestDto.getStatus());
        return ResponseMessage.SuccessResponse("상태 변경되었습니다.", "");
    }

    private Reservation findReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    private boolean checkReservationOwner(Reservation reservation, User user) {
        return reservation.getUser().getId().equals(user.getId()) && !reservation.getStatus().equals("waiting");
    }


    public List<ReservationResponseDto> getReservationList(User user, Long id){
        return reservationRepository.findByProductId(id).stream()
                .map(r -> new ReservationResponseDto(r.getId(), r.getStartDate(), r.getEndDate(), r.getStatus(),
                        r.getUser().getNickname(), checkReservationOwner(r, user))).toList();
    }


    public ResponseEntity<ResponseMessage> getAcceptedReservationList() {
        List<AcceptReservationResponseDto> acceptReservationList = reservationRepository.findByStatus("accepted").stream()
                .map(r -> new AcceptReservationResponseDto(r.getId(), r.getUser().getNickname(), r.getProduct().getUser().getNickname())).toList();
//        List<AcceptReservationResponseDto> acceptReservationList = reservationRepository.findAcceptedReservationDto();
//        acceptReservationList.forEach(r -> r.setOwnerNickname(userService.getUserByUserId(r.getOwnerNickname()).getNickname()));
//        acceptReservationList.forEach(r -> r.setCustomerNickname(userService.getUserByUserId(r.getCustomerNickname()).getNickname()));
        return ResponseMessage.SuccessResponse("검색완료 되었습니다.", acceptReservationList);
    }


    public List<RentResponseDto> getRentedItems(User user) {
        List<Reservation> reservationList = reservationRepository.findByUser(user);
        List<RentResponseDto> rentalResponseDtoList = reservationList.stream()
                .map(reservation -> new RentResponseDto(reservation,searchPrimeImageUrl(reservation)))
                .toList();

//        List<RentResponseDto> rentalResponseDtoList = new ArrayList<>();
//
//        for (Reservation reservation :reservationList) {
//            RentResponseDto rentResponseDto = new RentResponseDto(reservation);
//            rentalResponseDtoList.add(rentResponseDto);
//        }

        return rentalResponseDtoList;
    }
    private String searchPrimeImageUrl(Reservation reservation) {
        List<String> imageUrlList = imageStorageService.getImageUrlsByProductId(reservation.getProduct().getId());
        return imageUrlList.get(0);
    }
}









