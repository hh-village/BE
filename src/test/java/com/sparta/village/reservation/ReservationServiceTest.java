package com.sparta.village.reservation;

import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.reservation.repository.ReservationRepository;
import com.sparta.village.domain.reservation.service.ReservationService;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReservationServiceTest {
    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

//    @Autowired
//    private ProductRepository productRepository;

    @Test
    @DisplayName("예약하기-정상케이스")
    public void testReserve() {
        //product 생성

        //예약 요청 생성
        ReservationRequestDto requestDto = new ReservationRequestDto();
        requestDto.setStartDate(LocalDate.of(2023,4,10));
        requestDto.setEndDate(LocalDate.of(2023,4,13));

        //서비스 사용해서 예약
        ResponseEntity<ResponseMessage> response = reservationService.reserve(1L, requestDto, 1L);

        //예약 성공 체크
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "예약 되었습니다.");

        //데이터베이스에 잘 저장 됐는지 체크
        List<Reservation> reservationList = reservationRepository.findAll();
        assertEquals(reservationList.size(), 1);

        Reservation reservation = reservationList.get(0);
        assertEquals(reservation.getProductId(), 1L);
        assertEquals(reservation.getUserId(), 1L);
        assertEquals(reservation.getStartDate(), requestDto.getStartDate());
        assertEquals(reservation.getEndDate(), requestDto.getEndDate());
        assertEquals(reservation.getStatus(), "waiting");
    }

    @Test
    @DisplayName("예약하기-중복 날짜 예외 처리")
    public void testReserveDuplicateDate() {
        //예약 생성
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setProductId(1L);
        reservation.setUserId(1L);
        reservation.setStartDate(LocalDate.of(2023,4,10));
        reservation.setEndDate(LocalDate.of(2023,4,15));
        reservation.setStatus("waiting");
        reservationRepository.save(reservation);

        //예약 요청 생성
        ReservationRequestDto requestDto = new ReservationRequestDto();
        requestDto.setStartDate(LocalDate.of(2023,4,9));
        requestDto.setEndDate(LocalDate.of(2023,4,12));

        //에러메시지 체크
        assertThrows(CustomException.class, () -> reservationService.reserve(1L, requestDto, 1L));
    }

    @Test
    @DisplayName("예약 취소 - 정상케이스")
    public void testDeleteReservation() {
        //예약 생성
        Reservation reservation = new Reservation();
//        reservation.setId(1L);
        reservation.setProductId(1L);
        reservation.setUserId(1L);
        reservation.setStartDate(LocalDate.of(2023,4,10));
        reservation.setEndDate(LocalDate.of(2023,4,15));
        reservation.setStatus("waiting");
        reservationRepository.save(reservation);

        //서비스 사용해서 예약 취소
        ResponseEntity<ResponseMessage> response = reservationService.deleteReservation(reservation.getId(), 1L);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "예약 취소되었습니다.");

        //데이터베이스에 잘 삭제됐는지 확인
        List<Reservation> reservationList = reservationRepository.findAll();
        assertEquals(reservationList.size(), 0);
    }

    @Test
    @DisplayName("예약 취소 - 예약 번호가 다를 경우")
    public void testDeleteReservationByNotProperId() {
        //예약 생성
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setProductId(1L);
        reservation.setUserId(1L);
        reservation.setStartDate(LocalDate.of(2023,4,10));
        reservation.setEndDate(LocalDate.of(2023,4,15));
        reservation.setStatus("waiting");
        reservationRepository.save(reservation);

        //에러메시지 체크
        assertThrows(CustomException.class, () -> reservationService.deleteReservation(2L, 1L));
    }

    @Test
    @DisplayName("예약 취소 - 구매자가 아닌 경우")
    public void testDeleteReservationByInvalidUserId() {
        //예약 생성
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setProductId(1L);
        reservation.setUserId(1L);
        reservation.setStartDate(LocalDate.of(2023,4,10));
        reservation.setEndDate(LocalDate.of(2023,4,15));
        reservation.setStatus("waiting");
        reservationRepository.save(reservation);

        //에러메시지 체크
        assertThrows(CustomException.class, () -> reservationService.deleteReservation(reservation.getId(), 2L));
    }
}