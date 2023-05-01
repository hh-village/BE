package com.sparta.village.domain.reservation.repository;


import com.sparta.village.domain.product.entity.Product;

import com.sparta.village.domain.reservation.dto.ReservationCountResponseDto;
import com.sparta.village.domain.reservation.dto.UserLevelDto;
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

    @Query(value = "select count(r) > 0 from Reservation r where r.id = :id and r.product.user = :user")
    boolean checkProductOwner(@Param("id") Long id, @Param("user") User user);

    @Query(value = "select new com.sparta.village.domain.reservation.dto.UserLevelDto(r.startDate, r.endDate) " +
            "from Reservation r " +
            "left join Product p " +
            "on r.product.id = p.id " +
            "where p.user = :user and r.status = 'returned'")
    List<UserLevelDto> findUserLevelData(@Param("user") User user);

    @Modifying
    @Query(value = "update Reservation r set r.status = :status where r.id = :id")
    void updateStatus(@Param("id") Long reservationId, @Param("status") String status);

    @Query(value = "select r from Reservation r where r.status = :status and r.product.isDeleted = false")
    List<Reservation> findByStatus(@Param("status") String status);

    @Query(value = "SELECT new com.sparta.village.domain.reservation.dto.ReservationCountResponseDto(p.id, COUNT(*)) FROM Reservation r LEFT JOIN Product p ON r.product.id = p.id WHERE r.status = 'returned' and p.isDeleted = false group by r.product order by COUNT(*) DESC")
    List<ReservationCountResponseDto> countReservationWithProduct();

    List<Reservation> findByProductId(Long productId);

    List<Reservation> findByUser(User user);

    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.product.id = :productId")
    void deleteByProductId(@Param("productId") Long id);

    @Query(value = "select count(*) from reservation r left join product p on r.product_id = p.id where p.user_id = :user_id and r.status = :status and p.is_deleted = false", nativeQuery = true)
    int findReservationCountByUserAndStatus(@Param("user_id") Long id, @Param("status") String status);
}
