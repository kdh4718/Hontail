package com.hontail.back.cocktailDetail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CocktailIngredientDetailDto {
    private Integer cocktailIngredientId;
    private String ingredientQuantity;
    private IngredientDetailDto ingredient;
}