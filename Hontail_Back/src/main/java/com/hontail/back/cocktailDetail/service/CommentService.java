package com.hontail.back.cocktailDetail.service;

import com.hontail.back.cocktailDetail.dto.CommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getCocktailComments(Integer cocktailId); // 댓글 조회
    CommentDto addComment(Integer cocktailId, Integer userId, String content); // 댓글 작성
    void updateComment(Integer cocktailId, Integer commentId, Integer userId, String content); // 댓글 수정
    void deleteComment(Integer cocktailId, Integer commentId, Integer userId); // 댓글 삭제
}