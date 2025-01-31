package com.hontail.back.cocktailDetail.controller;

import com.hontail.back.cocktailDetail.dto.CocktailDetailDto;
import com.hontail.back.cocktailDetail.service.CocktailDetailService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class CocktailDetailController {

    @Autowired
    private CocktailDetailService cocktailDetailService;

    @GetMapping("/api/recipe/{cocktailId}")
    @Operation(summary = "칵테일 상세 정보 조회", description = "CocktailID 기반 상세 정보 조회")
    public CocktailDetailDto getCocktailDetail(@PathVariable Integer cocktailId) {
        return cocktailDetailService.getCocktailDetail(cocktailId);
    }
}