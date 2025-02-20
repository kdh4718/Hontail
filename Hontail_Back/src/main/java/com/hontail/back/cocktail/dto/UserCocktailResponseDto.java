package com.hontail.back.cocktail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserCocktailResponseDto {
    private List<CocktailSummaryDto> likedCocktails;
    private List<CocktailSummaryDto> recentViewedCocktails;
}