package com.hontail.back.bartender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatResponseDto {
    private String message;
    private boolean isCocktailRecommendation;
    private CocktailDto cocktail;
}

