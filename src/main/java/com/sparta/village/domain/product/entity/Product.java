package com.sparta.village.domain.product.entity;

import com.sparta.village.domain.product.dto.ProductRequestDto;
import com.sparta.village.domain.reservation.entity.Timestamped;
import com.sparta.village.domain.user.entity.User;
import lombok.*;

import javax.persistence.*;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private int zzimCount;

    @ManyToOne
    private User user;

    public void plusZzimCount() {
        this.zzimCount++;
    }

    public void minusZzimCount() {
        this.zzimCount--;
    }

    public Product(User user, ProductRequestDto productRequestDto) {
        this.title = productRequestDto.getTitle();
        this.description = productRequestDto.getDescription();
        this.price = productRequestDto.getPrice();
        this.location = productRequestDto.getLocation();
        this.user = user;
        this.zzimCount = 0;
    }

    public void update(ProductRequestDto productRequestDto) {
        this.title = productRequestDto.getTitle();
        this.description = productRequestDto.getDescription();
        this.price = productRequestDto.getPrice();
        this.location = productRequestDto.getLocation();
        this.user = user;
        this.zzimCount = 0;

    }
}