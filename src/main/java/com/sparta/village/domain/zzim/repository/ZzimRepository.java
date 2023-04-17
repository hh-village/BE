package com.sparta.village.domain.zzim.repository;

import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.zzim.entity.Zzim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ZzimRepository extends JpaRepository<Zzim, Long> {

    Zzim findByProductAndUser(Product product, User user);

    boolean existsByProductAndUser(Product product, User user);

    int countByProductId(Long productId);

    List<Zzim> findByUser(User user);

    int countByUser(User user);

    @Modifying
    @Query("DELETE FROM Zzim z WHERE z.product.id = :productId")
    void deleteByProductId(@Param("productId") Long Id);

}
