package com.hontail.back.visionApi.controller;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.visionApi.dto.CocktailIngredientCountDto;
import com.hontail.back.visionApi.dto.CocktailIngredientMatchDto;
import com.hontail.back.visionApi.service.IngredientMatchService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/vision")
public class IngredientMatchController {

    @Autowired
    private IngredientMatchService ingredientMatchService;

    @GetMapping("/ingredient-analyze")
    @Operation(description = "요청으로 들어온 재료를 기반으로 부족한 재료 내림차순으로 100개 조회")
    public List<CocktailIngredientMatchDto> getMatchedIngredientsSummary(Integer userId, @RequestParam(value = "ingredientNames") List<String> name) {
        return ingredientMatchService.getMatchedIngredientSummary(name, userId);
    }

}
