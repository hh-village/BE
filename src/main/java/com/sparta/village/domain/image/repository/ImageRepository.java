package com.sparta.village.domain.image.repository;


import com.sparta.village.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByProductId(Long id);

    List<Image> findByProductIdIn(List<Long> productIds);
}
