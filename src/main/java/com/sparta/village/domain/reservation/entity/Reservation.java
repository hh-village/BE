package com.sparta.village.domain.reservation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import com.sparta.village.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
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

    public Reservation(Product product, User user, ReservationRequestDto requestDto) {
        this.product = product;
        this.user = user;
        this.startDate = requestDto.getStartDate();
        this.endDate = requestDto.getEndDate();
        this.status = "waiting";
    }

}
