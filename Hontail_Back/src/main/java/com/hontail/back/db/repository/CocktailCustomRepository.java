package com.hontail.back.db.repository;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.ingredient.dto.IngredientSearchDto;

import java.util.List;

public interface CocktailCustomRepository {
    List<CocktailSummaryDto> findRecommendedCocktails(int userId, List<String> ingredients);
}
