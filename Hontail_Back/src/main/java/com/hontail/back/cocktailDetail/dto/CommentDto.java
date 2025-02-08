package com.hontail.back.cocktailDetail.dto;

import com.hontail.back.db.entity.Comment;
import com.hontail.back.db.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentDto {
    private Integer commentId;
    private Integer userId;
    private String userNickname;
    private String userEmail;
    private String userImageUrl;
    private String content;
    private String commentCreatedAt;

    @Builder
    public CommentDto(Integer commentId, Integer userId, String userNickname,
                      String userEmail, String userImageUrl,
                      String content, String commentCreatedAt) {
        this.commentId = commentId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.userEmail = userEmail;
        this.userImageUrl = userImageUrl;
        this.content = content;
        this.commentCreatedAt = commentCreatedAt;
    }

    public static CommentDto from(Comment comment) {
        User user = comment.getUser();
        return CommentDto.builder()
                .commentId(comment.getId())
                .userId(user.getId())
                .userNickname(user.getUserNickname())
                .userEmail(user.getUserEmail())
                .userImageUrl(user.getUserImageUrl())
                .content(comment.getContent())
                .commentCreatedAt(comment.getCommentCreatedAt().toString())
                .build();
    }
}