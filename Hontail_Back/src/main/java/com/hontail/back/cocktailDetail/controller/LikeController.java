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
import com.hontail.back.security.util.SecurityUtil;
import com.hontail.back.global.exception.CustomException;
import com.hontail.back.global.exception.ErrorCode;

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
            @ApiResponse(responseCode = "401", description = "로그인이 필요한 서비스입니다",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "이미 좋아요가 존재함"),
            @ApiResponse(responseCode = "404", description = "칵테일 또는 사용자를 찾을 수 없음")
    })
    public ResponseEntity<Integer> addLike(
            @PathVariable Integer cocktailId) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        return ResponseEntity.ok(likeService.addLike(cocktailId, userId));
    }

    @DeleteMapping("/{cocktailId}/likes")
    @Operation(summary = "칵테일 좋아요 취소")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요한 서비스입니다",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "좋아요 또는 칵테일을 찾을 수 없음")
    })
    public ResponseEntity<Integer> deleteLike(
            @PathVariable Integer cocktailId) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        return ResponseEntity.ok(likeService.deleteLike(cocktailId, userId));
    }
}