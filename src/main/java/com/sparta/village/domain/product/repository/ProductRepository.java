
package com.sparta.village.domain.product.repository;


import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;



public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByUser(User user);
    boolean existsByIdAndUserId(Long id, Long userId);

    List<Product> findByTitleContainingAndLocationContainingOrderByIdDesc(String title, String location);
    List<Product> findByTitleContainingOrderByIdDesc (String title);
    List<Product> findByLocationContainingOrderByIdDesc(String location);

    @Query("select p from Product p order by p.id desc")
    List<Product> findAllOrderByIdDesc();
    @Query(value = "select * from product order by rand() limit :count", nativeQuery = true)
    List<Product> findRandomProduct(@Param("count") int count);

}
