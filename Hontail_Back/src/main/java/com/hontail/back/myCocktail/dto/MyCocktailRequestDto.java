package com.hontail.back.myCocktail.dto;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MyCocktailRequestDto {

    private String makerNickname;
    private String cocktailName;
    private String description;
    private int alcoholContent;
    private String imageUrl;
    private int isCustom;
    private String baseSpirit;
    private List<IngredientRequestDto> ingredients;
    private List<RecipeRequestDto> recipes;

}
