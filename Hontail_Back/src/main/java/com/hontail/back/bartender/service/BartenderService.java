package com.hontail.back.bartender.service;

import com.hontail.back.bartender.dto.ChatResponseDto;

public interface BartenderService {
    ChatResponseDto getInitialGreeting(String userId, String nickname);
    ChatResponseDto chat(String userMessage, String userId, String nickname);
}