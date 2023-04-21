package com.sparta.village.reservation;

import com.sparta.village.domain.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import javax.transaction.Transactional;


@SpringBootTest
@Transactional
public class ReservationRepositoryTest {
    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("정상케이스")
    public void testCheckOverlapDateByProductId() {
        //예약 생성
//        Reservation reservation = new Reservation();
//        reservation.setId(1L);
//        reservation.setProductId(1L);
//        reservation.setUserId(1L);
//        reservation.setStartDate(LocalDate.now());
//        reservation.setEndDate(LocalDate.now().plusDays(4));
//        reservationRepository.save(reservation);
//
//        //겹치는 날짜 입력 했을 때 true 반환 하는지 확인
//        boolean checkOverlap = reservationRepository.checkOverlapDateByProductId(1L, LocalDate.now(), LocalDate.now().plusDays(2));
//        assertTrue(checkOverlap);
//
//        //안 겹치는 날짜 입력 했을 때 false 반환 하는지 확인
//        boolean checkNotOverlap = reservationRepository.checkOverlapDateByProductId(1L, LocalDate.now().plusDays(5), LocalDate.now().plusDays(7));
//        assertFalse(checkNotOverlap);
    }
}
