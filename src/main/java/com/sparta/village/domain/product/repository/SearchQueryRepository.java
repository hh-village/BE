package com.sparta.village.domain.product.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.entity.QProduct;
import com.sparta.village.domain.user.entity.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;


@Repository
public class SearchQueryRepository {
    private final JPAQueryFactory queryFactory;

    public SearchQueryRepository(EntityManager em)
    {
        this.queryFactory = new JPAQueryFactory(em);
    }
    QProduct product = QProduct.product;

    public List<Product> searchProduct(User user, String title, String location, Long lastId, int size) {

        return queryFactory
                .selectFrom(product)
                .where(
                        searchCondition(title, location),
                        product.id.lt(lastId)
                )
                .orderBy(product.id.desc())
                .limit(size)
                .fetch();
    }

    private BooleanExpression searchCondition(String title, String location) {

        BooleanExpression checkTitle = title != null ? product.title.containsIgnoreCase(title) : null;
        BooleanExpression checkLocation = location != null ? product.location.containsIgnoreCase(location) : null;

        return checkTitle == null ? checkLocation : checkLocation == null ? checkTitle : checkTitle.and(checkLocation);
    }


}
