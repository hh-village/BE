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
                   "WITH product_images AS ( " +
                        "SELECT product_id, image_url, " +
                            "ROW_NUMBER() OVER (PARTITION BY product_id ORDER BY id) as row_num " +
                        "FROM image " +
                        "WHERE is_deleted = false " +
                   ")" +
                   "SELECT 'product' as type, p.id as product_id, p.title as title, i.image_url as image, p.created_at as createdAt " +
                   "FROM users u " +
                   "LEFT JOIN product p ON p.user_id = u.id " +
                   "LEFT JOIN product_images i ON i.product_id = p.id AND i.row_num = 1 " +
                   "WHERE p.user_id = :userId AND p.is_deleted = false AND :key = 'products' ", nativeQuery = true)
    List<Object[]> findProductsByUserIdAndKey(@Param("userId") Long userId, @Param("key") String key);

    @Query(value =
                  "WITH product_images AS ( " +
                        "SELECT product_id, image_url, " +
                            "ROW_NUMBER() OVER (PARTITION BY product_id ORDER BY id) as row_num " +
                        "FROM image " +
                        "WHERE is_deleted = false " +
                  ")" +
                  "SELECT 'reservation' as type, r.id as reservation_id, p.id as product_id, p.title as title, i.image_url as image, r.start_date as startDate, r.end_date as endDate, r.status as status " +
                  "FROM users u " +
                  "JOIN reservation r ON u.id = r.user_id " +
                  "JOIN product p ON r.product_id = p.id " +
                  "LEFT JOIN product_images i ON i.product_id = p.id AND i.row_num = 1 " +
                  "WHERE u.id = :userId AND p.is_deleted = false AND :key = 'rents' " +
                  "GROUP BY r.id, p.id, i.image_url, r.start_date, r.end_date, r.status ", nativeQuery = true)
    List<Object[]> findReservationsByUserIdAndKey(@Param("userId") Long userId, @Param("key") String key);

    @Query(value =
            "WITH product_images AS ( " +
                    "SELECT product_id, image_url, " +
                    "ROW_NUMBER() OVER (PARTITION BY product_id ORDER BY id) as row_num " +
                    "FROM image " +
                    "WHERE is_deleted = false " +
                    ") " +
                    "SELECT 'zzim' as type, z.id as zzim_id, p.id as product_id, p.title as productTitle, i.image_url as image " +
                    "FROM users u " +
                    "JOIN zzim z ON u.id = z.user_id " +
                    "JOIN product p ON z.product_id = p.id " +
                    "LEFT JOIN product_images i ON i.product_id = p.id AND i.row_num = 1 " +
                    "WHERE z.user_id = :userId AND p.is_deleted = false AND :key = 'zzims' " +
                    "GROUP BY z.id, p.id, i.image_url ", nativeQuery = true)
    List<Object[]> findZzimsByUserIdAndKey(@Param("userId") Long userId, @Param("key") String key);
}
