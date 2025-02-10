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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.hontail.back.global.exception.ErrorResponse;

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
            @RequestParam(defaultValue = "false") boolean isCustom,
            @RequestParam(required = false) Integer userId
    ) {
        return cocktailService.getCocktailsByFilter(orderBy, direction, baseSpirit, page, size, isCustom, userId);
    }

    @GetMapping("/top-liked")
    @Operation(summary = "좋아요 상위 10개 칵테일 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "칵테일을 찾을 수 없음")
    })
    public ResponseEntity<List<CocktailSummaryDto>> getTopLikedCocktails(
            @RequestParam(required = false) Integer userId) {
        return ResponseEntity.ok(cocktailService.getTopLikedCocktails(userId));
    }

    @GetMapping("/liked")
    @Operation(summary = "사용자가 좋아요 한 칵테일 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음 또는 좋아요한 칵테일이 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<CocktailSummaryDto>> getLikedCocktails(
            @RequestParam Integer userId) {
        return ResponseEntity.ok(cocktailService.getLikedCocktails(userId));
    }

    @GetMapping("/search")
    @Operation(summary = "칵테일 이름으로 검색")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공"),
            @ApiResponse(responseCode = "400", description = "검색어가 비어있음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "검색 결과가 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Page<CocktailSummaryDto>> searchCocktails(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Integer userId) {
        return ResponseEntity.ok(cocktailService.searchCocktails(keyword, page, size, userId));
    }

}
