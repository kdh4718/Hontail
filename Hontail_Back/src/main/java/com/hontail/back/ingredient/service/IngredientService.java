package com.hontail.back.ingredient.service;

import com.hontail.back.db.entity.Ingredient;
import com.hontail.back.ingredient.dto.IngredientDto;

import java.util.List;

public interface IngredientService {
    public List<IngredientDto> getAllIngredients();
}
