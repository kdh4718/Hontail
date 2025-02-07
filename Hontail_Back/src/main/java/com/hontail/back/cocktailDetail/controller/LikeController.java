package com.hontail.back.cocktailDetail.controller;

import com.hontail.back.cocktailDetail.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cocktail/detail")
@CrossOrigin("*")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/{cocktailId}/likes")
    @Operation(summary = "칵테일 좋아요 추가")
    public ResponseEntity<?> addLike(
            @PathVariable Integer cocktailId,
            @RequestParam Integer userId) {
        likeService.addLike(cocktailId, userId);
        return ResponseEntity.ok().body("{\"statusCode\": \"200\", \"message\": \"좋아요가 추가되었습니다.\"}");
    }

    @DeleteMapping("/{cocktailId}/likes")
    @Operation(summary = "칵테일 좋아요 취소")
    public ResponseEntity<?> deleteLike(
            @PathVariable Integer cocktailId,
            @RequestParam Integer userId) {
        likeService.deleteLike(cocktailId, userId);
        return ResponseEntity.ok().body("{\"statusCode\": \"200\", \"message\": \"좋아요가 취소되었습니다.\"}");
    }
}