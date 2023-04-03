package com.sparta.village.domain.reservation.repository;

import com.sparta.village.domain.reservation.entity.Reservation;
import net.bytebuddy.asm.Advice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    //예약 하려는 날짜가 기존 날짜와 겹치는지 체크. 겹치면 true 반환.
    @Query(value = "select count(r) > 0 " +
            "from Reservation r " +
            "where r.productId = :id " +
            "and (r.startDate <= :endDate and r.endDate >= :startDate)")
    boolean checkOverlapDateByProductId(@Param("id") Long productId,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);
    boolean existsByUserId(Long userId);
}
