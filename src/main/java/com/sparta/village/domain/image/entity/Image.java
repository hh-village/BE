package com.sparta.village.domain.image.entity;

import com.sparta.village.domain.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.w3c.dom.Text;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 500)
    private String imageUrl;
    @Column(nullable = false)
    private Long productid;

    public Image(Product product, String imageUrl) {
        this.productid = product.getId();
        this.imageUrl = imageUrl;
    }

}
