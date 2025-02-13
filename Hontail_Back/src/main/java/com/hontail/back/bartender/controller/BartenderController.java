package com.hontail.back.bartender.controller;

import com.hontail.back.bartender.dto.ChatRequestDto;
import com.hontail.back.bartender.dto.ChatResponseDto;
import com.hontail.back.bartender.service.BartenderService;
import com.hontail.back.global.exception.ErrorResponse;
import com.hontail.back.oauth.CustomOAuth2User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bartender")
@Tag(name = "Bartender", description = "바텐더 API")
@RequiredArgsConstructor
public class BartenderController {

    private final BartenderService bartenderService;

    @GetMapping("/greeting")
    @Operation(summary = "바텐더 초기 인사말")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인사말 생성 성공"),
            @ApiResponse(responseCode = "500", description = "인사말 생성 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ChatResponseDto> greeting(@AuthenticationPrincipal CustomOAuth2User user) {
        Integer userId = user != null ? user.getUserId() : null;
        String nickname = user != null ? user.getName() : null;
        return ResponseEntity.ok(bartenderService.getInitialGreeting(userId, nickname));
    }

    @PostMapping("/chat")
    @Operation(summary = "바텐더 챗봇")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "응답 생성 성공"),
            @ApiResponse(responseCode = "400", description = "응답이 너무 김",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "응답 생성 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ChatResponseDto> chat(
            @RequestBody ChatRequestDto request,
            @AuthenticationPrincipal CustomOAuth2User user) {
        Integer userId = user != null ? user.getUserId() : null;
        String nickname = user != null ? user.getName() : null;
        return ResponseEntity.ok(bartenderService.chat(
                request.getUserMessage(),
                userId,
                nickname
        ));
    }
}