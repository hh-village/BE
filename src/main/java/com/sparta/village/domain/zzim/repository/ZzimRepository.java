package com.sparta.village.domain.zzim.repository;

import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.zzim.entity.Zzim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ZzimRepository extends JpaRepository<Zzim, Long> {
    Optional<Zzim> findByProductAndUser(Product product, User user);

    int countByProductId(Long productId);

}
