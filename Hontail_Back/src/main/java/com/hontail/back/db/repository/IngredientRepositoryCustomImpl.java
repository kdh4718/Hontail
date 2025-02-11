package com.hontail.back.db.repository;

import com.hontail.back.db.entity.QIngredient;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class IngredientRepositoryCustomImpl implements IngredientRepositoryCustom {

    @Autowired
    private JPAQueryFactory queryFactory;

    QIngredient qIngredient = QIngredient.ingredient;

    @Override
    public List<Integer> findByIngredientNames(List<String> ingredientNames) {
        BooleanBuilder builder = new BooleanBuilder();

        for (String ingredientName : ingredientNames) {
            builder.or(qIngredient.ingredientName.contains(ingredientName));
        }

        return queryFactory
                .select(qIngredient.id)
                .from(qIngredient)
                .where(builder)
                .fetch();
    }
}
