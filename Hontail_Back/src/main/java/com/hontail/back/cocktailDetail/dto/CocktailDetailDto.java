package com.hontail.back.cocktailDetail.dto;

import com.hontail.back.db.entity.Cocktail;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CocktailDetailDto {
    private Integer cocktailId;
    private String cocktailName;
    private String cocktailDescription;
    private String imageUrl;
    private List<CocktailIngredientDetailDto> cocktailIngredients;
    private List<RecipeDetailDto> recipes;
    private List<LikeDto> likes;
    private List<CommentDto> comments;

    @Builder
    public CocktailDetailDto(Integer cocktailId, String cocktailName, String cocktailDescription,
                             String imageUrl, List<CocktailIngredientDetailDto> cocktailIngredients,
                             List<RecipeDetailDto> recipes, List<LikeDto> likes,
                             List<CommentDto> comments) {
        this.cocktailId = cocktailId;
        this.cocktailName = cocktailName;
        this.cocktailDescription = cocktailDescription;
        this.imageUrl = imageUrl;
        this.cocktailIngredients = cocktailIngredients;
        this.recipes = recipes;
        this.likes = likes;
        this.comments = comments;
    }

    public static CocktailDetailDto from(Cocktail cocktail) {
        return CocktailDetailDto.builder()
                .cocktailId(cocktail.getId())
                .cocktailName(cocktail.getCocktailName())
                .cocktailDescription(cocktail.getCocktailDescription())
                .imageUrl(cocktail.getImageUrl())
                .cocktailIngredients(cocktail.getCocktailIngredients().stream()
                        .map(CocktailIngredientDetailDto::from)
                        .toList())
                .recipes(cocktail.getRecipes().stream()
                        .map(RecipeDetailDto::from)
                        .toList())
                .likes(cocktail.getLikes().stream()
                        .map(LikeDto::from)
                        .toList())
                .comments(cocktail.getComments().stream()
                        .map(CommentDto::from)
                        .toList())
                .build();
    }
}