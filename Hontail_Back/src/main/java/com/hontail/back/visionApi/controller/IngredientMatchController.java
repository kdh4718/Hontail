package com.hontail.back.visionApi.controller;

import com.hontail.back.visionApi.dto.CocktailIngredientCountDto;
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

    @GetMapping("")
    @Operation(description = "test")
    public List<Integer> getMatchedIngredientsByName(@RequestParam(value = "name") List<String> name) {
        return ingredientMatchService.getMatchedIngredientsId(name);
    }

    @GetMapping("/stats")
    @Operation(description = "stat test")
    public List<CocktailIngredientCountDto> getMatchedIngredientCounts(@RequestParam(value = "name") List<String> name) {
        return ingredientMatchService.getMatchedIngredientsCount(name);
    }

}
