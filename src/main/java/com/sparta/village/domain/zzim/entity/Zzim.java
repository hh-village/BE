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
    private int zzimCount = 0;

    @Column(nullable = false)
    private String productTitle;

    @Column(nullable = false)
    private boolean zzimStatus;

    @ManyToOne
    private Product product;
    @ManyToOne
    private User user;

    public void plusZzimCount() {
        this.zzimCount ++;
    }

    public void minusZzimCount() {
        this.zzimCount --;
    }

    public Zzim(User user, Product product, boolean zzimStatus) {
        this.zzimCount = getZzimCount();
        this.productTitle = product.getTitle();
        this.product = product;
        this.user = user;
        this.zzimStatus = zzimStatus;

    }

    public Product getProduct() {
        return product;
    }
}
