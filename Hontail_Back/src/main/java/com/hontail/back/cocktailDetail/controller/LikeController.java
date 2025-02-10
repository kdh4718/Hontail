package com.hontail.back.cocktailDetail.controller;

import com.hontail.back.cocktailDetail.service.LikeService;
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
@Tag(name = "Like", description = "좋아요 API")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/{cocktailId}/likes")
    @Operation(summary = "칵테일 좋아요 추가")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 추가 성공"),
            @ApiResponse(responseCode = "400", description = "이미 좋아요가 존재함",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "칵테일 또는 사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> addLike(
            @PathVariable Integer cocktailId,
            @RequestParam Integer userId) {
        likeService.addLike(cocktailId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cocktailId}/likes")
    @Operation(summary = "칵테일 좋아요 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
            @ApiResponse(responseCode = "404", description = "좋아요 또는 칵테일을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteLike(
            @PathVariable Integer cocktailId,
            @RequestParam Integer userId) {
        likeService.deleteLike(cocktailId, userId);
        return ResponseEntity.ok().build();
    }
}