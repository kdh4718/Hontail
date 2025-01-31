package com.hontail.back.cocktail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CocktailSummaryResponseDto {
    private List<CocktailSummaryDto> cocktailSummaryList;
}
