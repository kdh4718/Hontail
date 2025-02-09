package com.hontail.back.cocktailDetail.controller;

import com.hontail.back.cocktailDetail.dto.CocktailDetailDto;
import com.hontail.back.cocktailDetail.service.CocktailDetailService;
import com.hontail.back.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cocktail/detail")
@Tag(name = "Cocktail Detail", description = "칵테일 상세 API")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CocktailDetailController {

    private final CocktailDetailService cocktailDetailService;

    @GetMapping("/{cocktailId}")
    @Operation(summary = "칵테일 상세 정보 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "칵테일을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CocktailDetailDto> getCocktailDetail(@PathVariable Integer cocktailId) {
        return ResponseEntity.ok(cocktailDetailService.getCocktailDetail(cocktailId));
    }
}