package com.hontail.back.db.repository;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.db.entity.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CocktailCustomRepositoryImpl implements CocktailCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CocktailSummaryDto> findRecommendedCocktails(int userId, List<String> ingredientNames) {
        return null;
    }
}
