package com.sparta.village.domain.zzim.entity;

import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Zzim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String productTitle;
    @ManyToOne
    private Product product;
    @ManyToOne
    private User user;

    public Zzim(User user, Product product) {
        this.productTitle = product.getTitle();
        this.product = product;
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }
}
