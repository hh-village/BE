package com.sparta.village.domain.product.repository;


import com.sparta.village.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;



public interface ProductRepository extends JpaRepository<Product, Long> {


    List<Product> findByUserId(Long userId);


    boolean existsByIdAndUserId(Long id, Long userId);

    List<Product> findByTitleContainingAndLocationContaining(String title, String location);
    List<Product> findByTitleContaining (String title);
    List<Product> findByLocationContaining(String location);

}
