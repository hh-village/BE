package com.sparta.village.reservation;

import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ReservationRequestDtoTest {
    @Test
    public void testReservationRequestDto() {
        //dto 생성
        ReservationRequestDto reservationRequestDto = new ReservationRequestDto();

        reservationRequestDto.setStartDate(LocalDate.of(2023,4,10));
        reservationRequestDto.setEndDate(LocalDate.of(2023,4,12));

        assertEquals(String.valueOf(reservationRequestDto.getStartDate()), "2023-04-10");
        assertEquals(String.valueOf(reservationRequestDto.getEndDate()), "2023-04-12");

    }
}