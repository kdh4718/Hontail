package com.hontail.back.ingredient.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDto {
    private Integer ingredientId;
    private String ingredientName;
    private String ingredientNameKor;
    private String ingredientCategory;
    private String ingredientCategoryKor;
    private String ingredientType;
    private Integer ingredientAlcoholContent;
    private String ingredientImage;
}
