package com.hontail.back.cocktailDetail.dto;

import com.hontail.back.db.entity.Ingredient;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class IngredientDetailDto {
    private Integer ingredientId;
    private String ingredientNameKor;
    private String ingredientImage;

    @Builder
    public IngredientDetailDto(Integer ingredientId, String ingredientNameKor, String ingredientImage) {
        this.ingredientId = ingredientId;
        this.ingredientNameKor = ingredientNameKor;
        this.ingredientImage = ingredientImage;
    }

    public static IngredientDetailDto from(Ingredient ingredient) {
        return IngredientDetailDto.builder()
                .ingredientId(ingredient.getId())
                .ingredientNameKor(ingredient.getIngredientNameKor())
                .ingredientImage(ingredient.getIngredientImage())
                .build();
    }
}