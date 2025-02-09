package com.hontail.back.bartender.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequestDto {
    private String userMessage;
    private Integer userId;
    private String nickname;
}