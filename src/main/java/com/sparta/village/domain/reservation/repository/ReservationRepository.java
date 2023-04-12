package com.sparta.village.domain.reservation.repository;


import com.sparta.village.domain.product.entity.Product;

import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;


public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    //예약 하려는 날짜가 기존 날짜와 겹치는지 체크. 겹치면 true 반환.
    @Query(value = "select count(r) > 0 from Reservation r where r.product = :product " +
            "and (r.startDate <= :endDate and r.endDate >= :startDate)")
    boolean checkOverlapDateByProduct(@Param("product") Product product, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    boolean existsByUser(User user);

//    @Query(value = "select r.product from Reservation r where r.id = :id")
//    Product findProductIdById(@Param("id") Long reservationId);

    @Modifying
    @Query(value = "update Reservation r set r.status = :status where r.id = :id")
    void updateStatus(@Param("id") Long reservationId, @Param("status") String status);

//    @Query(value = "select " +
//            "new com.sparta.village.domain.reservation.dto.AcceptReservationResponseDto(r.id, concat(r.product, ''), concat(r.user, '')) " +
//            "from Reservation r WHERE r.status = 'accepted'")
//    List<AcceptReservationResponseDto> findAcceptedReservationDto();

    List<Reservation> findByStatus(String status);


    List<Reservation> findByUser(User user);
}






