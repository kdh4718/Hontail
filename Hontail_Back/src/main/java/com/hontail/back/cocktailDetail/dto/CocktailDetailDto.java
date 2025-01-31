package com.hontail.back.cocktailDetail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CocktailDetailDto {
    private Integer cocktailId;
    private String cocktailName;
    private String cocktailDescription;
    private String imageUrl;
    private List<CocktailIngredientDetailDto> ingredients;
    private List<RecipeDetailDto> recipes;
    private List<LikeDto> likes;
    private List<CommentDto> comments;
}