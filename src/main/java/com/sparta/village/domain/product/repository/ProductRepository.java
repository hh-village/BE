
package com.sparta.village.domain.product.repository;

import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;



public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByUser(User user);
    boolean existsByIdAndUserId(Long id, Long userId);

    List<Product> findByTitleContainingAndLocationContainingOrderByIdDesc(String title, String location);
    List<Product> findByTitleContainingOrderByIdDesc (String title);
    List<Product> findByLocationContainingOrderByIdDesc(String location);

    @Query(value = "select * from product where is_deleted = false order by id desc limit 6", nativeQuery = true)
    List<Product> findLatestSixProduct();

    @Query(value = "select * from product where id != :id and is_deleted = false order by rand() limit 8", nativeQuery = true)
    List<Product> findRandomEightProduct(@Param("id") Long id);


    @Modifying
    @Query(value = "update product p " +
            "left join image on p.id = image.product_id " +
            "left join chat_room on p.id = chat_room.product_id " +
            "left join reservation on p.id = reservation.product_id " +
            "left join zzim on p.id = zzim.product_id " +
            "set p.is_deleted = true, " +
            "image.is_deleted = true, " +
            "chat_room.is_deleted = true, " +
            "reservation.is_deleted = true, " +
            "zzim.is_deleted = true " +
            "where p.id = :id", nativeQuery = true)
    void deleteAllAboutProductById(@Param("id") Long id);

    @Query(value = "SELECT p.id, p.title, p.description, p.price, p.location, u.id as owner_id, u.nickname, u.profile, p.zzim_count, " +
            "(SELECT COUNT(*) FROM reservation r WHERE r.user_id = u.id AND r.status = 'returned') as owner_returned, " +
            "(SELECT COUNT(*) FROM reservation r WHERE r.user_id = u.id AND r.status = 'accepted') as owner_accepted, " +
            "(SELECT COUNT(*) FROM reservation r WHERE r.user_id = u.id AND r.status = 'waiting') as owner_waiting, " +
            "EXISTS(SELECT 1 FROM zzim z WHERE (:userId IS NULL OR z.user_id = :userId) AND z.product_id = p.id) as zzim_status, " +
            "i.image_url, " +
            "r.id as reservation_id, r.start_date, r.end_date, r.status as reservation_status, r.user_id as reservation_user_id, ru.nickname as reservation_user_nickname, ru.profile as reservation_user_profile " +
            "FROM product p " +
            "JOIN users u ON p.user_id = u.id " +
            "JOIN image i ON p.id = i.product_id " +
            "JOIN reservation r ON p.id = r.product_id " +
            "JOIN users ru ON r.user_id = ru.id " +
            "WHERE p.id = :productId", nativeQuery = true)
    List<Object[]> findProductDetailList(@Param("productId") Long productId, @Param("userId") Long userId);
}
