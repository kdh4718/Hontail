package com.hontail.back.cocktailDetail.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private Integer userId;
    private String makerNickname;
    private Integer alcoholContent;
    private Integer likeCnt;
    private Integer commentCnt;
    @JsonProperty("isLiked")
    private boolean liked;
    private List<CocktailIngredientDetailDto> cocktailIngredients;
    private List<RecipeDetailDto> recipes;


    @Builder
    public CocktailDetailDto(Integer cocktailId, String cocktailName, String cocktailDescription,
                             String imageUrl, Integer userId, String makerNickname,
                             Integer alcoholContent, Integer likeCnt,
                             Integer commentCnt, boolean liked,
                             List<CocktailIngredientDetailDto> cocktailIngredients,
                             List<RecipeDetailDto> recipes) {
        this.cocktailId = cocktailId;
        this.cocktailName = cocktailName;
        this.cocktailDescription = cocktailDescription;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.makerNickname = makerNickname;
        this.alcoholContent = alcoholContent;
        this.likeCnt = likeCnt;
        this.commentCnt = commentCnt;
        this.liked = liked;
        this.cocktailIngredients = cocktailIngredients;
        this.recipes = recipes;
    }

    public static CocktailDetailDto from(Cocktail cocktail, Long likeCount, boolean isLiked) {
        return CocktailDetailDto.builder()
                .cocktailId(cocktail.getId())
                .cocktailName(cocktail.getCocktailName())
                .cocktailDescription(cocktail.getCocktailDescription())
                .imageUrl(cocktail.getImageUrl())
                .userId(cocktail.getUser().getId())
                .makerNickname(cocktail.getMakerNickname())
                .alcoholContent(cocktail.getAlcoholContent())
                .likeCnt(likeCount.intValue())
                .commentCnt(cocktail.getComments().size())
                .liked(isLiked)
                .cocktailIngredients(cocktail.getCocktailIngredients().stream()
                        .map(CocktailIngredientDetailDto::from)
                        .toList())
                .recipes(cocktail.getRecipes().stream()
                        .map(RecipeDetailDto::from)
                        .toList())
                .build();
    }
}