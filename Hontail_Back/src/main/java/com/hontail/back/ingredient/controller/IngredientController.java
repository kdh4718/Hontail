package com.hontail.back.ingredient.controller;

import com.hontail.back.ingredient.dto.IngredientDto;
import com.hontail.back.ingredient.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@CrossOrigin("*")
public class IngredientController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping("")
    @Operation(description = "모든 재료 조회")
    public List<IngredientDto> getAllIngredients() {
        return ingredientService.getAllIngredients();
    }

}
