package com.sparta.village.domain.product.entity;

import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.product.dto.ProductRequestDto;
import com.sparta.village.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private String location;
    @Column(nullable = false)
    private Long userId;

    public Product(User user, ProductRequestDto productRequestDto) {
        this.title = productRequestDto.getTitle();
        this.description = productRequestDto.getDescription();
        this.price = productRequestDto.getPrice();
        this.location = productRequestDto.getLocation();
        this.userId = user.getId();
    }
}
