package com.hontail.back.ingredient.service;

import com.hontail.back.db.entity.Ingredient;
import com.hontail.back.db.repository.IngredientRepository;
import com.hontail.back.ingredient.dto.IngredientDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IngredientServiceImpl implements IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Override
    public List<IngredientDto> getAllIngredients() {

        return ingredientRepository.findAll().stream()
                .map(ingredient -> IngredientDto.builder()
                        .ingredientId(ingredient.getId())
                        .ingredientName(ingredient.getIngredientName())
                        .ingredientNameKor(ingredient.getIngredientNameKor())
                        .ingredientCategory(ingredient.getIngredientCategory())
                        .ingredientCategoryKor(ingredient.getIngredientCategoryKor())
                        .ingredientType(ingredient.getIngredientType())
                        .ingredientAlcoholContent(ingredient.getIngredientAlcoholContent())
                        .ingredientImage(ingredient.getIngredientImage())
                        .build())
                .collect(Collectors.toList());
    }
}
