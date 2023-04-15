package com.sparta.village.domain.zzim.service;

import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.zzim.entity.Zzim;
import com.sparta.village.domain.zzim.repository.ZzimRepository;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ZzimService {
    private final ProductRepository productRepository;
    private final ZzimRepository zzimRepository;
    @Transactional
    public ResponseEntity<ResponseMessage> zzim(Long id, User user) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)
        );

        Optional<Zzim> getZzim = zzimRepository.findByProductAndUser(product, user);

        if(getZzim.isEmpty()) {
            zzimRepository.save(new Zzim(user, product));
            product.plusZzimCount();
            productRepository.save(product);
            return ResponseMessage.SuccessResponse("찜하기 성공", true);
        }else {
            zzimRepository.delete(getZzim.get());
            product.minusZzimCount();
            productRepository.save(product);
            return ResponseMessage.SuccessResponse("찜하기 취소", false);
        }
    }
}
