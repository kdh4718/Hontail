package com.hontail.back.visionApi.service;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.repository.CocktailIngredientRepository;
import com.hontail.back.db.repository.CocktailRepository;
import com.hontail.back.db.repository.IngredientRepository;
import com.hontail.back.db.repository.LikeRepository;
import com.hontail.back.visionApi.dto.CocktailIngredientCountDto;
import com.hontail.back.visionApi.dto.CocktailIngredientMatchDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class IngredientMatchService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private CocktailRepository cocktailRepository;

    @Autowired
    private CocktailIngredientRepository cocktailIngredientRepository;

    @Autowired
    private LikeRepository likeRepository;

    public List<Integer> getMatchedIngredientsId(List<String> ingredientNames) {
        return ingredientRepository.findByIngredientNames(ingredientNames);
    }

    public List<CocktailIngredientCountDto> getMatchedIngredientsCount(List<String> ingredientNames) {
        List<Integer> ingredientIds = getMatchedIngredientsId(ingredientNames);
        return cocktailRepository.findCocktailIngredientStats(ingredientIds);
    }

    public List<CocktailIngredientMatchDto> getMatchedIngredientSummary(List<String> ingredientNames, Integer userId) {
        // 매칭 재료가 1개 이상인 칵테일들 리스트
        List<CocktailIngredientCountDto> cocktailCountStats = getMatchedIngredientsCount(ingredientNames);

        List<CocktailIngredientCountDto> sortedCocktailStats = cocktailCountStats.stream()
                .sorted(Comparator.comparing(CocktailIngredientCountDto::getMissingIngredients))
                .limit(40)
                .toList();

        List<Integer> cocktailIds = sortedCocktailStats.stream()
                .map(CocktailIngredientCountDto::getCocktailId)
                .toList();

        List<Cocktail> cocktails = cocktailRepository.findAllByIdIn(cocktailIds);

        Map<Integer, CocktailIngredientCountDto> cocktailStatsMap = sortedCocktailStats.stream()
                .collect(Collectors.toMap(CocktailIngredientCountDto::getCocktailId, dto -> dto));

        return cocktails.stream()
                .map(cocktail -> {
                    Long ingredientCnt = cocktailIngredientRepository.countByCocktail(cocktail);
                    Long likesCnt = likeRepository.countByCocktail(cocktail);

                    boolean isLiked = false;
                    if (userId != null) {
                        isLiked = likeRepository.findByCocktailIdAndUserId(cocktail.getId(), userId).isPresent();
                    }

                    Long missingIngredients = cocktailStatsMap.get(cocktail.getId()).getMissingIngredients();

                    return new CocktailIngredientMatchDto(
                            cocktail.getId(),
                            cocktail.getCocktailName(),
                            cocktail.getImageUrl(),
                            likesCnt,
                            cocktail.getAlcoholContent(),
                            cocktail.getBaseSpirit(),
                            cocktail.getCreatedAt(),
                            ingredientCnt,
                            isLiked,
                            missingIngredients
                    );

                })
                .sorted(Comparator.comparing(CocktailIngredientMatchDto::getMissingIngredients))
                .toList();
    }
}
