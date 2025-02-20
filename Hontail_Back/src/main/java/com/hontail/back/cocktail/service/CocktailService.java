package com.hontail.back.cocktail.service;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.cocktail.dto.TopLikedCocktailDto;
import com.hontail.back.cocktail.dto.UserCocktailResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CocktailService {

    public Page<CocktailSummaryDto> getCocktailsByFilter(
            @RequestParam(required = false, defaultValue = "id", value = "orderBy") String orderBy,
            @RequestParam(required = false, defaultValue = "asc", value = "direction") String direction,
            @RequestParam(required = false) String baseSpirit,
            int page, int size, boolean isCustom, Integer userId);

    List<TopLikedCocktailDto> getTopLikedCocktails(); // 사용자가 좋아요 한 칵테일 (사용하지 않으나 혹시 몰라 남겨둠)

    List<CocktailSummaryDto> getLikedCocktails(Integer userId);

    Page<CocktailSummaryDto> searchCocktails(String keyword, int page, int size, Integer userId);

    Page<CocktailSummaryDto> getMyCocktails(Integer userId, int page, int size); // 나만의 칵테일 리스트

    // 사용자가 좋아요 한 칵테일과 최근 본 칵테일 목록
    UserCocktailResponseDto getLikedAndRecentCocktails(Integer userId, List<Integer> recentCocktailIds);
}
