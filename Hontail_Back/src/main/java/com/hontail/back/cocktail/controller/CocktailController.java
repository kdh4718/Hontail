package com.hontail.back.cocktail.controller;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.cocktail.service.CocktailService;
import com.hontail.back.db.repository.CocktailSummaryProjection;
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

    @GetMapping("")
    @Operation(summary = "좋아요, 알코올, 등록일 순으로 정렬하여 조회")
    public Page<CocktailSummaryDto> getFilteredCocktails(
            @RequestParam(required = false) String baseSpirit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "cocktail_id" ) String sortBy,
            @RequestParam(defaultValue = "asc" ) String sortDirection
    ) {
        return cocktailService.getFilteredCocktails(page, size, baseSpirit, sortBy, sortDirection);
    }

}
