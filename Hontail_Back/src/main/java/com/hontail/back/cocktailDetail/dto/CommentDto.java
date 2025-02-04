package com.hontail.back.cocktailDetail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Integer commentId;
    private Integer userId;
    private String userNickname;
    private String userEmail;
    private String userImageUrl;
    private String content;
    private String commentCreatedAt;
}