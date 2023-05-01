package com.sparta.village.domain.image.entity;

import com.sparta.village.domain.product.entity.Product;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;


import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "update image set is_deleted = true where id = ?")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    @ManyToOne
    private Product product;

    private boolean isDeleted = Boolean.FALSE;

    public Image(Product product, String imageUrl) {
        this.product = product;
        this.imageUrl = imageUrl;
    }

}
