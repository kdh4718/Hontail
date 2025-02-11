package com.hontail.back.cocktail.service;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.cocktail.dto.TopLikedCocktailDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CocktailService {

    public Page<CocktailSummaryDto> getCocktailsByFilter(
            @RequestParam(required = false, defaultValue = "id", value = "orderBy") String orderBy,
            @RequestParam(required = false, defaultValue = "asc", value = "direction") String direction,
            @RequestParam(required = false) String baseSpirit,
            int page, int size, boolean isCustom, Integer userId);

    List<TopLikedCocktailDto> getTopLikedCocktails();

    List<CocktailSummaryDto> getLikedCocktails(Integer userId);

    Page<CocktailSummaryDto> searchCocktails(String keyword, int page, int size, Integer userId);

//    Page<CocktailSummaryDto> getMyCocktails(Integer userId, int page, int size); // 내가 만든 칵테일 조회

    List<CocktailSummaryDto> getMyCocktails(Integer userId);
}
