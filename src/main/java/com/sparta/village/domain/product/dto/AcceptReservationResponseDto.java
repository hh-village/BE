package com.sparta.village.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AcceptReservationResponseDto {
    private Long id;
    private String customerNickname;
    private String ownerNickname;
}
