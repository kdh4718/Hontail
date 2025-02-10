package com.hontail.back.db.repository;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.ingredient.dto.IngredientSearchDto;
import com.hontail.back.visionApi.dto.CocktailIngredientCountDto;

import java.util.List;

public interface CocktailCustomRepository {
    List<CocktailIngredientCountDto> findCocktailIngredientStats(List<Integer> inputIngredientIds);
}
