package com.hontail.back.cocktail.service;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.db.repository.CocktailSummaryProjection;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CocktailService {
    public Page<CocktailSummaryDto> getFilteredCocktails(int page, int size, String baseSpirit, String sortBy, String sortDirection);
}
