package com.sparta.village.domain.zzim.entity;

import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "update zzim set is_deleted = true where id = ?")
public class Zzim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Product product;
    @ManyToOne
    private User user;

    private boolean isDeleted = Boolean.FALSE;

    public Zzim(User user, Product product) {
        this.product = product;
        this.user = user;

    }
}
