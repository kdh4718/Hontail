package com.hontail.back.cocktailDetail.dto;

import com.hontail.back.db.entity.CocktailIngredient;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CocktailIngredientDetailDto {
    private Integer cocktailIngredientId;
    private String ingredientQuantity;
    private IngredientDetailDto ingredient;

    @Builder
    public CocktailIngredientDetailDto(Integer cocktailIngredientId, String ingredientQuantity,
                                       IngredientDetailDto ingredient) {
        this.cocktailIngredientId = cocktailIngredientId;
        this.ingredientQuantity = ingredientQuantity;
        this.ingredient = ingredient;
    }

    public static CocktailIngredientDetailDto from(CocktailIngredient cocktailIngredient) {
        return CocktailIngredientDetailDto.builder()
                .cocktailIngredientId(cocktailIngredient.getId())
                .ingredientQuantity(cocktailIngredient.getIngredientQuantity())
                .ingredient(IngredientDetailDto.from(cocktailIngredient.getIngredient()))
                .build();
    }
}