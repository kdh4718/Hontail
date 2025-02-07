package com.hontail.back.cocktailDetail.controller;

import com.hontail.back.cocktailDetail.dto.CommentDto;
import com.hontail.back.cocktailDetail.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cocktail/detail")
@CrossOrigin("*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/{cocktailId}/comments")
    @Operation(summary = "칵테일 댓글 조회", description = "CocktailID 기반 칵테일 댓글 조회")
    public ResponseEntity<List<CommentDto>> getCocktailComments(
            @PathVariable Integer cocktailId) {
        List<CommentDto> comments = commentService.getCocktailComments(cocktailId);
        return ResponseEntity.ok(comments); // 상태 코드 : 200 OK
    }

    @PostMapping("/{cocktailId}/comments")
    @Operation(summary = "칵테일 댓글 작성")
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Integer cocktailId,
            @RequestBody CommentDto commentDto) {
        CommentDto createdComment = commentService.addComment(cocktailId, commentDto.getUserId(), commentDto.getContent());
        return ResponseEntity.status(201).body(createdComment); // 상태 코드 : 201 Created
    }

    @PutMapping("/{cocktailId}/comments/{commentId}")
    @Operation(summary = "칵테일 댓글 수정")
    public ResponseEntity<?> updateComment(
            @PathVariable Integer cocktailId,
            @PathVariable Integer commentId,
            @RequestBody CommentDto commentDto) {
        commentService.updateComment(cocktailId, commentId, commentDto.getUserId(), commentDto.getContent());
        return ResponseEntity.ok().body("{\"statusCode\": \"200\", \"message\": \"댓글이 수정되었습니다.\"}");
        // CRUD 중 수정부분만 구체적으로 표시함(어떤 작업이 성공했는지 명확히하기 위해서..)
    }

    @DeleteMapping("/{cocktailId}/comments/{commentId}")
    @Operation(summary = "칵테일 댓글 삭제")
    public ResponseEntity<?> deleteComment(
            @PathVariable Integer cocktailId,
            @PathVariable Integer commentId,
            @RequestParam Integer userId) {
        commentService.deleteComment(cocktailId, commentId, userId);
        return ResponseEntity.noContent().build();  // 상태 코드 : 204 NoContent
    }
}