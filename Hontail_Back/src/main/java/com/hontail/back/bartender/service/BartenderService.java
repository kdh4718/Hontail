package com.hontail.back.bartender.service;

import com.hontail.back.bartender.dto.ChatResponseDto;

public interface BartenderService {
    ChatResponseDto getInitialGreeting(Integer userId, String nickname);
    ChatResponseDto chat(String userMessage, Integer userId, String nickname);
}