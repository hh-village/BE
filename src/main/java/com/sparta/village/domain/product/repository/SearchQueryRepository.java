package com.sparta.village.domain.product.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.village.domain.product.dto.ProductResponseDto;
import com.sparta.village.domain.product.entity.QProduct;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Repository
public class SearchQueryRepository {
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    public SearchQueryRepository(EntityManager em)
    {
        this.em = em;
        this.queryFactory = new JPAQueryFactory(em);
    }

    public List<ProductResponseDto> searchProduct(Long userId, String title, String location, Long lastId, int size) {
        String sql = "SELECT product.id, title, " +
                " (SELECT image_url FROM image where image.product_id = product.id and is_deleted = false limit 1) AS image_url, " +
                "   location, " +
                "   price, " +
                "  (WITH reservationCounts AS ( " +
                "     SELECT p.id AS productId, p.title, r.id AS reservationId, COUNT(*) AS count " +
                "     FROM product p " +
                "     LEFT JOIN reservation r ON p.id = r.product_id " +
                "     WHERE p.is_deleted = false AND r.is_deleted = false AND r.status = 'returned' " +
                "     GROUP BY p.id " +
                "     ORDER BY count(*) DESC " +
                "   ), lastIndex AS ( " +
                "     SELECT CEIL(COUNT(*) * 0.1) AS lastIndex " +
                "     FROM reservationCounts " +
                "   ) " +
                "   SELECT EXISTS(SELECT * FROM ( " +
                "     SELECT RANK() OVER(ORDER BY rc.count DESC) AS ranking, " +
                "            rc.productId, rc.title, rc.reservationId, " +
                "            rc.count, lastIndex.lastIndex " +
                "     FROM reservationCounts rc " +
                "     CROSS JOIN lastIndex " +
                "   ) AS rankingList " +
                "   WHERE ranking <= lastIndex AND productId = product.id)) AS checkHot, " +
                "   (SELECT EXISTS (SELECT p.id FROM product p " +
                "                  LEFT JOIN zzim ON p.id = zzim.product_id " +
                "                  WHERE p.is_deleted = false AND p.id = product.id AND zzim.user_id = :userId)) AS checkZzim " +
                "FROM product " +
                "where product.id < :lastId and product.is_deleted = false " +
                "and (lower(product.title) like lower(:title) or :title is null) " +
                "and (lower(product.location) like lower(:location) or :location is null) " +
                "order by product.id desc limit :size";

        Query query = em.createNativeQuery(sql);
        query.setParameter("userId", userId);
        query.setParameter("lastId", lastId);
        query.setParameter("size", size);
        query.setParameter("title", title != null ? "%" + title + "%" : null);
        query.setParameter("location", location != null ? "%" + location + "%" : null);

        List<Object[]> results = query.getResultList();

        return results.stream().map(p ->
                new ProductResponseDto(Long.parseLong(p[0].toString()), (String)p[1], (String)p[2], (String)p[3], Integer.parseInt(String.valueOf(p[4])), Integer.parseInt(String.valueOf(p[5])) == 1, Integer.parseInt(String.valueOf(p[6])) == 1)).toList();
    }

}
