package com.hontail.back.bartender.controller;

import com.hontail.back.bartender.dto.ChatRequestDto;
import com.hontail.back.bartender.dto.ChatResponseDto;
import com.hontail.back.bartender.service.BartenderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bartender")
@CrossOrigin("*")
@RequiredArgsConstructor
public class BartenderController {
    private final BartenderService bartenderService;

    @GetMapping("/greeting")
    @Operation(summary = "바텐더 초기 인사말")
    public ChatResponseDto greeting(
            @RequestParam String userId,
            @RequestParam String nickname) {
        return bartenderService.getInitialGreeting(userId, nickname);
    }

    @PostMapping("/chat")
    @Operation(summary = "바텐더 챗봇")
    public ChatResponseDto chat(@RequestBody ChatRequestDto request) {
        return bartenderService.chat(
                request.getUserMessage(),
                request.getUserId(),
                request.getNickname()
        );
    }
}