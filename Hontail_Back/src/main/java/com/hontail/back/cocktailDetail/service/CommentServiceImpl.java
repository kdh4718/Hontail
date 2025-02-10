package com.hontail.back.cocktailDetail.service;

import com.hontail.back.cocktailDetail.dto.CommentDto;
import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.entity.Comment;
import com.hontail.back.db.entity.User;
import com.hontail.back.db.repository.CocktailRepository;
import com.hontail.back.db.repository.CommentRepository;
import com.hontail.back.db.repository.UserRepository;
import com.hontail.back.global.exception.CustomException;
import com.hontail.back.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CocktailRepository cocktailRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    // 댓글 조회
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCocktailComments(Integer cocktailId) {
        Cocktail cocktail = cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new CustomException(ErrorCode.COCKTAIL_DETAIL_NOT_FOUND));

        return cocktail.getComments().stream()
                .map(CommentDto::from)
                .toList();
    }

    // 댓글 작성
    @Override
    @Transactional
    public CommentDto addComment(Integer cocktailId, Integer userId, String content) {
        // 댓글 내용 검증
        if (content == null || content.trim().isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_CONTENT_EMPTY);
        }
        if (content.length() > 200) {  // 최대 200자 제한
            throw new CustomException(ErrorCode.COMMENT_TOO_LONG);
        }
        Cocktail cocktail = cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new CustomException(ErrorCode.COCKTAIL_DETAIL_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Comment comment = new Comment();
        comment.setCocktail(cocktail);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCommentCreatedAt(Instant.now());

        return CommentDto.from(commentRepository.save(comment));
    }

    // 댓글 수정
    @Override
    @Transactional
    public void updateComment(Integer cocktailId, Integer commentId, Integer userId, String content) {
        // 댓글 내용 검증
        if (content == null || content.trim().isEmpty()) {
            throw new CustomException(ErrorCode.COMMENT_CONTENT_EMPTY);
        }
        if (content.length() > 200) {  // 최대 200자 제한
            throw new CustomException(ErrorCode.COMMENT_TOO_LONG);
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
        }

        comment.setContent(content);
    }

    // 댓글 삭제
    @Override
    @Transactional
    public void deleteComment(Integer cocktailId, Integer commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_COMMENT_ACCESS);
        }

        commentRepository.delete(comment);
    }
}