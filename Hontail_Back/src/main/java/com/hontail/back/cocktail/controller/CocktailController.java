package com.hontail.back.cocktail.controller;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.cocktail.service.CocktailService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cocktail")
@CrossOrigin("*")
public class CocktailController {

    @Autowired
    CocktailService cocktailService;

    @GetMapping("/filtering")
    @Operation(summary = "칵테일 필터링 조회")
    public Page<CocktailSummaryDto> getCocktailsByFilter(
            @RequestParam(required = false, defaultValue = "id") String orderBy,
            @RequestParam(required = false, defaultValue = "asc", value = "direction") String direction,
            @RequestParam(required = false) String baseSpirit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return cocktailService.getCocktailsByFilter(orderBy, direction, baseSpirit, page, size);
    }

}
