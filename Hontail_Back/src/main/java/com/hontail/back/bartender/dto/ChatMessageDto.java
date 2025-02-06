package com.hontail.back.bartender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatMessageDto {
    private String role;    // 사용자 : 'user', 바텐더 : 'assistant' 역할
    private String content;
}