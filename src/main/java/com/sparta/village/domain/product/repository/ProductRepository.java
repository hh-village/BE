
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

}
