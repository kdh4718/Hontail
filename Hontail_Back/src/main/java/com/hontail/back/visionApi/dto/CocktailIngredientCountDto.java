package com.hontail.back.visionApi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CocktailIngredientCountDto {
    private Integer cocktailId;
    private Long totalIngredients;
    private Long matchingIngredients;
    private Long missingIngredients;

    @Override
    public String toString() {
        return "CocktailIngredientCountDto{" +
                "cocktailId=" + cocktailId +
                ", totalIngredients=" + totalIngredients +
                ", matchingIngredients=" + matchingIngredients +
                ", missingIngredients=" + missingIngredients +
                '}';
    }
}
