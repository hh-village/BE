package com.sparta.village.domain.image.entity;

import com.sparta.village.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @ManyToOne
    private Product product;

    public Image(Product product, String imageUrl) {
        this.product = product;
        this.imageUrl = imageUrl;
    }

}
