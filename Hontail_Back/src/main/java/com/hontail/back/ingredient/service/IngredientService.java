package com.hontail.back.ingredient.service;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.db.entity.Ingredient;
import com.hontail.back.ingredient.dto.IngredientDto;
import com.hontail.back.ingredient.dto.IngredientSearchDto;

import java.util.List;

public interface IngredientService {
    public List<IngredientDto> getAllIngredients();

    public List<CocktailSummaryDto> getRecommendedCocktails(Integer userId, List<String> ingredients);
}
