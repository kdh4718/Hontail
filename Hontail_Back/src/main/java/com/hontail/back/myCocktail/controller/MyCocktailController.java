package com.hontail.back.myCocktail.controller;

import com.hontail.back.myCocktail.dto.MyCocktailRequestDto;
import com.hontail.back.myCocktail.service.MyCocktailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/custom-cocktail")
@CrossOrigin
public class MyCocktailController {

    @Autowired
    private MyCocktailService myCocktailService;

    @PostMapping
    @Operation(
            summary = "나만의 칵테일 등록",
            description = """
            사용자가 직접 칵테일을 등록하는 API입니다.
            요청이 성공하면 추가된 칵테일의 ID를 반환합니다. \s
            <br>
            **✅ 요청 데이터 예시:** \s
            ```json
            {
              "makerNickname": "Bartender123",
              "name": "Mojito", 
              "description": "A refreshing cocktail",
              "alcoholContent": 12,
              "imageUrl": "https://example.com/mojito.jpg",
              "isCustom": true,
              "baseSpirit": "Rum",
              "ingredients": [
                {"ingredientId": 1, "quantity": "50ml"},
                {"ingredientId": 2, "quantity": "10ml"}
              ],
              "recipes": [
                {"guide": "Mix ingredients", "sequence": 1},
                {"guide": "Serve over ice", "sequence": 2}
              ]
            }
            ```
           \s"""
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 칵테일이 등록됨",
                    content = @Content(schema = @Schema(implementation = Integer.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "유저 ID가 존재하지 않음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<Integer> createMyCocktail(
            @io.swagger.v3.oas.annotations.Parameter(description = "등록하는 유저의 ID", example = "1")
            @RequestParam int userId,

            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "등록할 칵테일 데이터",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MyCocktailRequestDto.class))
            )
            MyCocktailRequestDto requestDto
    ) {
        int cocktailId = myCocktailService.createMyCocktail(userId, requestDto);
        return ResponseEntity.ok(cocktailId);
    }

}
