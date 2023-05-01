package com.sparta.village.domain.reservation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import com.sparta.village.domain.user.entity.User;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Where(clause = "is_deleted = false")
@SQLDelete(sql = "update reservation set is_deleted = true where id = ?")
public class Reservation extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String status; //waiting, accepted, rejected, returned

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;

    @ManyToOne
    private User user;

    @ManyToOne
    private Product product;

    private boolean isDeleted = Boolean.FALSE;

    public Reservation(Product product, User user, ReservationRequestDto requestDto) {
        this.product = product;
        this.user = user;
        this.startDate = requestDto.getStartDate();
        this.endDate = requestDto.getEndDate();
        this.status = "waiting";
    }

}
