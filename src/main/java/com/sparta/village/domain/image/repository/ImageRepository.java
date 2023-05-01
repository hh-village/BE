package com.sparta.village.domain.image.repository;


import com.sparta.village.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.font.ImageGraphicAttribute;
import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query(value="select i from Image i where i.product.id = :id and i.product.isDeleted = false")
    List<Image> findByProductId(@Param("id") Long id);

    List<Image> findFirstByProductId(Long productId);
}
