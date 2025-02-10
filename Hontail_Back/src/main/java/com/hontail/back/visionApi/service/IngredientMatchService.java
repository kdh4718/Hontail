package com.hontail.back.visionApi.service;

import com.hontail.back.db.repository.CocktailRepository;
import com.hontail.back.db.repository.IngredientRepository;
import com.hontail.back.visionApi.dto.CocktailIngredientCountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IngredientMatchService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private CocktailRepository cocktailRepository;

    public List<Integer> getMatchedIngredientsId(List<String> ingredientNames) {
        return ingredientRepository.findByIngredientNames(ingredientNames);
    }

    public List<CocktailIngredientCountDto> getMatchedIngredientsCount(List<String> ingredientNames) {
        List<Integer> ingredientIds = getMatchedIngredientsId(ingredientNames);


        return cocktailRepository.findCocktailIngredientStats(ingredientIds);
    }

}
