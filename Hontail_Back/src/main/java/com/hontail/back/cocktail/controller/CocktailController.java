package com.hontail.back.cocktail.controller;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.cocktail.service.CocktailService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@RestController
@RequestMapping("/api/cocktail")
@Tag(name = "Cocktail", description = "칵테일 API")
@CrossOrigin("*")
public class CocktailController {

    @Autowired
    CocktailService cocktailService;

    @GetMapping("/filtering")
    @Operation(summary = "칵테일 필터링 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "404", description = "칵테일을 찾을 수 없음")
    })
    public Page<CocktailSummaryDto> getCocktailsByFilter(
            @RequestParam(required = false, defaultValue = "id") String orderBy,
            @RequestParam(required = false, defaultValue = "asc", value = "direction") String direction,
            @RequestParam(required = false) String baseSpirit,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean isCustom
    ) {
        return cocktailService.getCocktailsByFilter(orderBy, direction, baseSpirit, page, size, isCustom);
    }

    @GetMapping("/top-liked")
    @Operation(summary = "좋아요 상위 10개 칵테일 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "칵테일을 찾을 수 없음")
    })
    public ResponseEntity<List<CocktailSummaryDto>> getTopLikedCocktails() {
        List<CocktailSummaryDto> topCocktails = cocktailService.getTopLikedCocktails();
        return ResponseEntity.ok(topCocktails);
    }

}
