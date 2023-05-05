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

        if(!getZzim(product, user)) {
            zzimRepository.save(new Zzim(user, product));
            product.plusZzimCount();
            productRepository.save(product);
            return ResponseMessage.SuccessResponse("찜하기 성공", getZzimStatus(user, product));
        }else {
            zzimRepository.delete(zzimRepository.findByProductAndUser(product, user));
            product.minusZzimCount();
            productRepository.save(product);
            return ResponseMessage.SuccessResponse("찜하기 취소", getZzimStatus(user, product));
        }
    }
    public boolean getZzim (Product product, User user) {
        return zzimRepository.existsByProductAndUser(product, user);
    }

    public boolean getZzimStatus(User user, Product product) {
        return user != null && zzimRepository.existsByProductAndUser(product, user);
    }

    public int getZzimCount(Long userId) {
        return userId != null ? zzimRepository.countByUserId(userId) : 0;
    }

    public int countByProductId(Long id) {
        return zzimRepository.countByProductId(id);
    }

    public void deleteByProductId(Long id) {
        zzimRepository.deleteByProductId(id);
    }
}
