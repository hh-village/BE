package com.sparta.village.domain.product.repository;


import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;



public interface ProductRepository extends JpaRepository<Product, Long> {


    List<Product> findByUserId(Long userId);
    List<Product> findAllByUser(User user);
    boolean existsByIdAndUserId(Long id, Long userId);

    List<Product> findByTitleContainingAndLocationContaining(String title, String location);
    List<Product> findByTitleContaining (String title);
    List<Product> findByLocationContaining(String location);

    List<Product> findByReservation(Reservation reservation);

}
