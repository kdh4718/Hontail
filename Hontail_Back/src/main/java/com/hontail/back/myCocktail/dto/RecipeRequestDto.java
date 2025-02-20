package com.hontail.back.myCocktail.dto;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequestDto {
    private String recipeGuide;
    private Integer sequence;
    private String recipeAction;

}
