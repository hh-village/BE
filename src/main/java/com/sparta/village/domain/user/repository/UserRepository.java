package com.sparta.village.domain.user.repository;


import com.sparta.village.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKakaoId(Long kakaoId);

    Optional<User> findByNickname(String nickname);

    @Query(value =
            "SELECT 'product' as type, p.id as product_id, p.title as title, i.image_url as image, p.created_at as createdAt " +
                    "FROM users u " +
                    "JOIN product p ON p.user_id = u.id " +
                    "LEFT JOIN image i ON i.product_id = p.id " +
                    "WHERE p.user_id = :userId AND :key = 'products' " +
                    "GROUP BY p.id, i.image_url, p.title, p.created_at ", nativeQuery = true)
    List<Object[]> findProductsByUserIdAndKey(@Param("userId") Long userId, @Param("key") String key);

    @Query(value =
            "SELECT 'reservation' as type, r.id as reservation_id, p.id as product_id, p.title as title, i.image_url as image, r.start_date as startDate, r.end_date as endDate, r.status as status " +
                    "FROM users u " +
                    "JOIN reservation r ON u.id = r.user_id " +
                    "JOIN product p ON r.product_id = p.id " +
                    "LEFT JOIN image i ON i.product_id = p.id " +
                    "WHERE u.id = :userId AND i.is_deleted = false AND :key = 'rents' " +
                    "GROUP BY r.id, p.id, i.image_url, r.start_date, r.end_date, r.status ", nativeQuery = true)
    List<Object[]> findReservationsByUserIdAndKey(@Param("userId") Long userId, @Param("key") String key);

    @Query(value =
            "SELECT 'zzim' as type, z.id as zzim_id, p.id as product_id, p.title as productTitle, i.image_url as image " +
                    "FROM users u " +
                    "JOIN zzim z ON u.id = z.user_id " +
                    "JOIN product p ON z.product_id = p.id " +
                    "LEFT JOIN image i ON i.product_id = p.id " +
                    "WHERE z.user_id = :userId AND i.is_deleted = false AND :key = 'zzims' " +
                    "GROUP BY z.id, p.id, i.image_url ", nativeQuery = true)
    List<Object[]> findZzimsByUserIdAndKey(@Param("userId") Long userId, @Param("key") String key);
}
