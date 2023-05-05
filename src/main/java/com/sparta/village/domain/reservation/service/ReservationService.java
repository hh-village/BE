package com.sparta.village.domain.reservation.service;

import com.sparta.village.domain.product.dto.AcceptReservationResponseDto;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.dto.*;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.reservation.repository.ReservationRepository;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.service.UserService;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Duration;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Transactional
    public ResponseEntity<ResponseMessage> reserve(Long productId, ReservationRequestDto requestDto, User user) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        if (reservationRepository.checkOverlapDateByProduct(product, requestDto.getStartDate(), requestDto.getEndDate())) {
            throw new CustomException(ErrorCode.DUPLICATE_RESERVATION_DATE);
        }
        reservationRepository.saveAndFlush(new Reservation(product, user, requestDto));
        return ResponseMessage.SuccessResponse("예약 되었습니다.", "");
    }

    @Transactional
    public ResponseEntity<ResponseMessage> deleteReservation(Long id, User user) {
        Reservation reservation = findReservationById(id);
        if (!checkReservationOwner(reservation, user)) {
            throw new CustomException(ErrorCode.NOT_AUTHOR);
        }
        reservationRepository.deleteById(id);
        return ResponseMessage.SuccessResponse("예약 취소되었습니다.", "");
    }

    @Transactional
    public ResponseEntity<ResponseMessage> changeStatus(Long id, StatusRequestDto requestDto, User user) {
        Reservation reservation = findReservationById(id);
        if (!reservationRepository.checkProductOwner(id, user)) {
            throw new CustomException(ErrorCode.NOT_SELLER);
        }
        reservationRepository.updateStatus(reservation.getId(), requestDto.getStatus());
        checkAndUpdateProfile(user);
        return ResponseMessage.SuccessResponse("상태 변경되었습니다.", "");
    }

    private Reservation findReservationById(Long id) {
        return reservationRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));
    }

    private boolean checkReservationOwner(Reservation reservation, User user) {
        return user != null && reservation.getUser().getId().equals(user.getId()) && reservation.getStatus().equals("waiting");
    }


    public List<ReservationResponseDto> getReservationList(User user, Long id){
        return reservationRepository.findByProductId(id).stream()
                .map(r -> new ReservationResponseDto(r.getId(), r.getStartDate(), r.getEndDate(), r.getStatus(),
                        r.getUser().getNickname(), r.getUser().getProfile(), checkReservationOwner(r, user))).toList();
    }

    public List<AcceptReservationResponseDto> getAcceptedReservationList() {
        return reservationRepository.findByStatus("accepted").stream()
                .map(r -> new AcceptReservationResponseDto(r.getId(), r.getUser().getNickname(), r.getProduct().getUser().getNickname())).toList();
    }

    public List<ReservationCountResponseDto> reservationCount() {
        return reservationRepository.countReservationWithProduct();
    }

    public void deleteByProductId(Long id) {
        reservationRepository.deleteByProductId(id);
    }

    private void checkAndUpdateProfile(User user) {
        List<UserLevelDto> userLevelDtoList = reservationRepository.findUserLevelData(user);
        long count = userLevelDtoList.size();
        long totalDate = 0L;
        for (UserLevelDto userLevelDto : userLevelDtoList) {
            totalDate += Duration.between(userLevelDto.getStartDate().atStartOfDay(), userLevelDto.getEndDate().atStartOfDay()).toDays();
        }
        String profile = (totalDate > 81 && count > 8) ? "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile5.png" :
                        (totalDate > 27 && count > 6) ? "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile4.png" :
                        (totalDate > 9 && count > 4) ? "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile3.png" :
                        (totalDate > 3 && count > 2) ? "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile2.png" :
                        "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile1.png";
//        System.out.println(profile);
        userService.updateProfileIfNeeded(user, profile);
    }

    public int getReservationCountByUser(User owner, String status) {
        return reservationRepository.findReservationCountByUserAndStatus(owner.getId(), status);
    }
}









