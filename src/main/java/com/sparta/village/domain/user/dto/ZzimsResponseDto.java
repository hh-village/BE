package com.sparta.village.domain.user.dto;

import com.sparta.village.domain.image.entity.Image;
import com.sparta.village.domain.image.repository.ImageRepository;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.zzim.entity.Zzim;
import lombok.Getter;

@Getter
public class ZzimsResponseDto {

    private final Long id;
    private final String productTitle;
    private final String image;

    public ZzimsResponseDto(User user, Zzim zzim, ImageRepository imageRepository) {
        this.id = zzim.getProduct().getId();
        this.productTitle = zzim.getProduct().getTitle();
        this.image = imageRepository.findFirstByProductId(zzim.getProduct().getId())
                                       .map(Image::getImageUrl)
                                       .orElse(null);
    }
}
