package com.hontail.back.cocktailDetail.dto;

import com.hontail.back.db.entity.Recipe;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecipeDetailDto {
    private Integer recipeId;
    private String recipeGuide;
    private Integer sequence;
    private String recipeAction;

    @Builder
    public RecipeDetailDto(Integer recipeId, String recipeGuide, Integer sequence, String recipeAction) {
        this.recipeId = recipeId;
        this.recipeGuide = recipeGuide;
        this.sequence = sequence;
        this.recipeAction = recipeAction;
    }

    public static RecipeDetailDto from(Recipe recipe) {
        return RecipeDetailDto.builder()
                .recipeId(recipe.getId())
                .recipeGuide(recipe.getRecipeGuide())
                .sequence(recipe.getSequence())
                .recipeAction(recipe.getRecipeAction())
                .build();
    }
}