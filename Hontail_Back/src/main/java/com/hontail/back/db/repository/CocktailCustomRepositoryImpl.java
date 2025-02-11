package com.hontail.back.db.repository;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.db.entity.*;
import com.hontail.back.visionApi.dto.CocktailIngredientCountDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CocktailCustomRepositoryImpl implements CocktailCustomRepository {

    @Autowired
    private JPAQueryFactory queryFactory;

    QCocktail qCocktail = QCocktail.cocktail;
    QCocktailIngredient qCocktailIngredient = QCocktailIngredient.cocktailIngredient;
    QIngredient qIngredient = QIngredient.ingredient;

    @Override
    public List<CocktailIngredientCountDto> findCocktailIngredientStats(List<Integer> inputIngredientIds) {
        List<Tuple> results = queryFactory
                .select(
                        qCocktail.id,
                        qCocktailIngredient.ingredient.id.countDistinct().as("totalIngredients"),
                        new CaseBuilder()
                                .when(qCocktailIngredient.ingredient.id.in(inputIngredientIds))
                                .then(1)
                                .otherwise(0)
                                .sum()
                                .as("matchingIngredients")
                )
                .from(qCocktail)
                .join(qCocktailIngredient).on(qCocktail.id.eq(qCocktailIngredient.cocktail.id))
                .groupBy(qCocktail.id)
                .fetch();

        return results.stream().map(tuple -> {
            Integer cocktailId = tuple.get(qCocktail.id);

            Number totalIngredientsNum = tuple.get(1, Number.class);
            Long totalIngredients = totalIngredientsNum != null ? totalIngredientsNum.longValue() : 0L;

            Number matchingIngredientsNum = tuple.get(2, Number.class);
            Long matchingIngredients = matchingIngredientsNum != null ? matchingIngredientsNum.longValue() : 0L;

            Long missingIngredients = totalIngredients - matchingIngredients; // 부족한 재료 개수 계산

            return new CocktailIngredientCountDto(cocktailId, totalIngredients, matchingIngredients, missingIngredients);
        }).filter(dto -> dto.getMatchingIngredients() > 0).collect(Collectors.toList());

    }
}
