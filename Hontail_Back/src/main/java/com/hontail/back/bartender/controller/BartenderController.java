package com.hontail.back.bartender.controller;

import com.hontail.back.bartender.dto.ChatRequestDto;
import com.hontail.back.bartender.dto.ChatResponseDto;
import com.hontail.back.bartender.service.BartenderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.hontail.back.global.exception.ErrorResponse;
import com.hontail.back.security.util.SecurityUtil;
import com.hontail.back.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/bartender")
@Tag(name = "Bartender", description = "바텐더 API")
@RequiredArgsConstructor
public class BartenderController {

    private final BartenderService bartenderService;
    private final MyPageService myPageService;

    @GetMapping("/greeting")
    @Operation(summary = "바텐더 초기 인사말")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "인사말 생성 성공"),
            @ApiResponse(responseCode = "500", description = "인사말 생성 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ChatResponseDto> greeting() {
        Integer userId = SecurityUtil.getCurrentUserId();
        String nickname = null;
        if (userId != null) {
            nickname = myPageService.getCurrentUserById(userId).getNickname();
        }
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
    public ResponseEntity<ChatResponseDto> chat(@RequestBody ChatRequestDto request) {
        Integer userId = SecurityUtil.getCurrentUserId();
        String nickname = null;
        if (userId != null) {
            nickname = myPageService.getCurrentUserById(userId).getNickname();
        }
        return ResponseEntity.ok(bartenderService.chat(
                request.getUserMessage(),
                userId,
                nickname
        ));
    }
}