package com.hontail.back.cocktailDetail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDetailDto {
    private Integer recipeId;
    private String recipeGuide;
    private Integer sequence;
    private String recipeAction;
}