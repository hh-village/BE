package com.sparta.village.domain.product.entity;

import com.sparta.village.domain.chat.entity.ChatRoom;
import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.product.dto.ProductRequestDto;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.reservation.entity.Timestamped;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.zzim.entity.Zzim;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "update product set is_deleted = true where id = ?")
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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Zzim> zzimList;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Reservation> reservationList;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<Image> imageList;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ChatRoom> chatRoomListList;

    private boolean isDeleted = Boolean.FALSE;

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