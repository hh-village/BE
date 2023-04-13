package com.sparta.village.domain.image.repository;


import com.sparta.village.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByProductId(Long id);

    List<Image> findByProductIdIn(List<Long> productIds);

    Optional<Image> findFirstByProductId(Long productId);
}
