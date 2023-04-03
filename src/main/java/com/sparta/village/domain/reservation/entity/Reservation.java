package com.sparta.village.domain.reservation.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class Reservation extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long productId;

    public Reservation(Long productId, Long userId, ReservationRequestDto requestDto) {
        this.productId = productId;
        this.userId = userId;
        this.startDate = requestDto.getStartDate();
        this.endDate = requestDto.getEndDate();
    }
}
