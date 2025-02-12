package com.hontail.back.cocktailDetail.controller;

import com.hontail.back.cocktailDetail.dto.CommentDto;
import com.hontail.back.cocktailDetail.service.CommentService;
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

import java.util.List;

@RestController
@RequestMapping("/api/cocktail/detail")
@Tag(name = "Comment", description = "댓글 API")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/{cocktailId}/comments")
    @Operation(summary = "칵테일 댓글 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "칵테일을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable Integer cocktailId) {
        return ResponseEntity.ok(commentService.getCocktailComments(cocktailId));
    }

    @PostMapping("/{cocktailId}/comment")
    @Operation(summary = "칵테일 댓글 작성")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공"),
            @ApiResponse(responseCode = "404", description = "칵테일 또는 사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Integer cocktailId,
            @RequestBody String content) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        return ResponseEntity.status(201).body(commentService.addComment(cocktailId, userId, content));
    }

    @PutMapping("/{cocktailId}/comments/{commentId}")
    @Operation(summary = "칵테일 댓글 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "403", description = "댓글 작성자가 아님",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> updateComment(
            @PathVariable Integer cocktailId,
            @PathVariable Integer commentId,
            @RequestBody String content) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        commentService.updateComment(cocktailId, commentId, userId, content);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cocktailId}/comments/{commentId}")
    @Operation(summary = "칵테일 댓글 삭제")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "댓글 작성자가 아님",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer cocktailId,
            @PathVariable Integer commentId) {
        Integer userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_USER);
        }
        commentService.deleteComment(cocktailId, commentId, userId);
        return ResponseEntity.ok().build();
    }
}