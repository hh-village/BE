
package com.sparta.village.domain.product.repository;

import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.dto.AcceptReservationResponseDto;
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

    @Query(value = "SELECT product.id, title, " +
            "(SELECT image_url FROM image where image.product_id = product.id and image.is_deleted = false limit 1) AS image_url, " +
            " location, " +
            " price, " +
            " (WITH reservationCounts AS (" +
            "   SELECT p.id AS productId, p.title, r.id AS reservationId, COUNT(*) AS count " +
            "   FROM product p " +
            "   LEFT JOIN reservation r ON p.id = r.product_id " +
            "   WHERE p.is_deleted = false AND r.is_deleted = false AND r.status = 'returned' " +
            "   GROUP BY p.id " +
            "   ORDER BY count(*) DESC" +
            "  ), lastIndex AS (" +
            "    SELECT CEIL(COUNT(*) * 0.1) AS lastIndex " +
            "    FROM reservationCounts " +
            "  )" +
            " SELECT EXISTS(SELECT * FROM ( " +
            "                             SELECT RANK() OVER(ORDER BY rc.count DESC) AS ranking, " +
            "                               rc.productId, rc.title, rc.reservationId, rc.count, lastIndex.lastIndex " +
            "                             FROM reservationCounts rc " +
            "                             CROSS JOIN lastIndex " +
            "                               ) AS rankingList " +
            " WHERE ranking <= lastIndex AND productId = product.id)) AS checkHot, " +
            "(SELECT EXISTS (SELECT p.id FROM product p " +
            "                LEFT JOIN zzim ON p.id = zzim.product_id " +
            "                WHERE p.is_deleted = false AND p.id = product.id AND zzim.user_id = :userId AND zzim.is_deleted = false)) AS checkZzim " +
            "FROM product " +
            "JOIN (SELECT id FROM product WHERE is_deleted = false ORDER BY id desc LIMIT 6) AS lastestIds " +
            "ON product.id = lastestIds.id ", nativeQuery = true)
    List<Object[]> findLatestSixProduct(@Param("userId") Long userId);

    @Query(value = "SELECT product.id, title, " +
            "(SELECT image_url FROM image where image.product_id = product.id and image.is_deleted = false limit 1) AS image_url, " +
            " location, " +
            " price, " +
            " (WITH reservationCounts AS (" +
            "   SELECT p.id AS productId, p.title, r.id AS reservationId, COUNT(*) AS count " +
            "   FROM product p " +
            "   LEFT JOIN reservation r ON p.id = r.product_id " +
            "   WHERE p.is_deleted = false AND r.is_deleted = false AND r.status = 'returned' " +
            "   GROUP BY p.id " +
            "   ORDER BY count(*) DESC" +
            "  ), lastIndex AS (" +
            "    SELECT CEIL(COUNT(*) * 0.1) AS lastIndex " +
            "    FROM reservationCounts " +
            "  )" +
            " SELECT EXISTS(SELECT * FROM ( " +
            "                             SELECT RANK() OVER(ORDER BY rc.count DESC) AS ranking, " +
            "                               rc.productId, rc.title, rc.reservationId, rc.count, lastIndex.lastIndex " +
            "                             FROM reservationCounts rc " +
            "                             CROSS JOIN lastIndex " +
            "                               ) AS rankingList " +
            " WHERE ranking <= lastIndex AND productId = product.id)) AS checkHot, " +
            "(SELECT EXISTS (SELECT p.id FROM product p " +
            "                LEFT JOIN zzim ON p.id = zzim.product_id " +
            "                WHERE p.is_deleted = false AND p.id = product.id AND zzim.user_id = :userId AND zzim.is_deleted = false)) AS checkZzim " +
            "FROM product " +
            "JOIN (SELECT id FROM product WHERE is_deleted = false and product.id != :productId ORDER BY RAND() LIMIT 8) AS randomIds " +
            "ON product.id = randomIds.id ", nativeQuery = true)
    List<Object[]> findRandomEightProduct(@Param("userId") Long userId, @Param("productId") Long productId);


    @Query(value = "SELECT product.id, title, " +
            " (SELECT image_url FROM image where image.product_id = product.id and is_deleted = false limit 1) AS image_url, " +
            "   location, " +
            "   price, " +
            "   (SELECT EXISTS (SELECT p.id FROM product p " +
            "                  LEFT JOIN zzim ON p.id = zzim.product_id " +
            "                  WHERE p.is_deleted = false AND p.id = :productId AND zzim.user_id = :userId)) AS checkZzim " +
            "FROM product " +
            "where product.id = :productId", nativeQuery = true)
    Object[] getProductResponseDtoByProductId(@Param("productId") Long productId, @Param("userId") Long userId);


    @Query(value = "with one_popular as ( " +
            "WITH reservationCounts AS ( " +
            "  SELECT p.id as productId, p.title, r.id as reservationId, COUNT(*) AS count " +
            "  FROM product p " +
            "  LEFT JOIN reservation r ON p.id = r.product_id " +
            "  WHERE p.is_deleted = false AND r.is_deleted = false AND r.status = 'returned' " +
            "  GROUP BY p.id " +
            "  ORDER BY count(*) DESC " +
            "), lastIndex AS ( " +
            "  SELECT ceil(count(*) * 0.1) AS lastIndex " +
            "  FROM reservationCounts " +
            ") " +
            "select * from( " +
            "  SELECT rank() over(order by rc.count desc) AS ranking, " +
            "   rc.productId, rc.title, rc.reservationId, " +
            "   rc.count, lastIndex.lastIndex " +
            " FROM reservationCounts rc " +
            " cross join lastIndex " +
            ") as rankingList " +
            "where ranking <= lastIndex " +
            "order by rand() limit 1) " +
            "SELECT product.id, title, " +
            " (SELECT image_url FROM image where image.product_id = product.id and is_deleted = false limit 1) AS image_url, " +
            "   location, " +
            "   price, " +
            "   (SELECT EXISTS (SELECT p.id FROM product p " +
            "                  LEFT JOIN zzim ON p.id = zzim.product_id " +
            "                  WHERE p.is_deleted = false AND p.id = (select productId from one_popular) AND zzim.user_id = :userId AND zzim.is_deleted = false)) AS checkZzim " +
            "FROM product " +
            "where product.id = (select productId from one_popular)", nativeQuery = true)
    List<Object[]> getOnePopularProduct(@Param("userId") Long userId);




    @Query(value = "select new com.sparta.village.domain.product.dto.AcceptReservationResponseDto(r.id, u2.nickname, u1.nickname) from Reservation r " +
            "left join Product p on r.product.id = p.id " +
            "inner join users u1 on p.user.id = u1.id " +
            "left join users u2 on r.user.id = u2.id " +
            "where r.status = 'accepted' and r.isDeleted = false ")
    List<AcceptReservationResponseDto> getDealList();

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
            "EXISTS(SELECT 1 FROM zzim z WHERE z.user_id = :userId AND z.product_id = p.id AND z.is_deleted = false) as zzim_status, " +
            "EXISTS(SELECT 1 FROM product pr WHERE pr.user_id = :userId AND pr.id = p.id) as checkOwner, " +
            "i.image_url, " +
            "r.id as reservation_id, r.start_date, r.end_date, r.status as reservation_status, r.user_id as reservation_user_id, ru.nickname as reservation_user_nickname, ru.profile as reservation_user_profile " +
            "FROM product p " +
            "left JOIN users u ON p.user_id = u.id " +
            "left JOIN image i ON p.id = i.product_id " +
            "left JOIN reservation r ON p.id = r.product_id " +
            "left JOIN users ru ON r.user_id = ru.id " +
            "WHERE p.id = :productId and i.is_deleted = false ", nativeQuery = true)
    List<Object[]> findProductDetailList(@Param("productId") Long productId, @Param("userId") Long userId);
}
