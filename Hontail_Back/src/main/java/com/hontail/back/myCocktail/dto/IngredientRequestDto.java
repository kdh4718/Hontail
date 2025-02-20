package com.hontail.back.myCocktail.dto;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientRequestDto {
    private int ingredientId;
    private String ingredientQuantity;

}
