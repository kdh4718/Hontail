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

@RestController
@RequestMapping("/api/bartender")
@Tag(name = "Bartender", description = "바텐더 API")
@RequiredArgsConstructor
public class BartenderController {
    private final BartenderService bartenderService;

    @GetMapping("/greeting")
    @Operation(summary = "바텐더 초기 인사말")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<ChatResponseDto> greeting(
            @RequestParam(required = true) Integer userId,
            @RequestParam(required = true) String nickname) {
        return ResponseEntity.ok(bartenderService.getInitialGreeting(userId, nickname));
    }

    @PostMapping("/chat")
    @Operation(summary = "바텐더 챗봇")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    public ResponseEntity<ChatResponseDto> chat(@RequestBody ChatRequestDto request) {
        return ResponseEntity.ok(bartenderService.chat(
                request.getUserMessage(),
                request.getUserId(),
                request.getNickname()
        ));
    }
}