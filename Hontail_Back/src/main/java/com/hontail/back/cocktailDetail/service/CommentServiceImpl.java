package com.hontail.back.cocktailDetail.service;

import com.hontail.back.cocktailDetail.dto.CommentDto;
import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.entity.Comment;
import com.hontail.back.db.entity.User;
import com.hontail.back.db.repository.CocktailRepository;
import com.hontail.back.db.repository.CommentRepository;
import com.hontail.back.db.repository.UserRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import java.time.Instant;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CocktailRepository cocktailRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository; 

    @Override
    public List<CommentDto> getCocktailComments(Integer cocktailId) {
        return cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new RuntimeException("칵테일을 찾을 수 없습니다."))
                .getComments().stream()
                .map(c -> {
                    User user = c.getUser(); // 댓글 작성자 정보
                    return new CommentDto(
                            c.getId(),
                            user.getId(),
                            user.getUserNickname(),
                            user.getUserEmail(),
                            user.getUserImageUrl(),
                            c.getContent(),
                            c.getCommentCreatedAt().toString()
                    );
                })
                .toList();
    }

    @Override
    @Transactional 
    public CommentDto addComment(Integer cocktailId, Integer userId, String content) {
        Cocktail cocktail = cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new RuntimeException("칵테일을 찾을 수 없습니다."));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다.")); 

        Comment comment = new Comment();
        comment.setCocktail(cocktail);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCommentCreatedAt(Instant.now());

        commentRepository.save(comment); // 댓글 저장

        return new CommentDto(
                comment.getId(),
                user.getId(),
                user.getUserNickname(),
                user.getUserEmail(),
                user.getUserImageUrl(),
                comment.getContent(),
                comment.getCommentCreatedAt().toString()
        );
    }

    @Override
    @Transactional 
    public void updateComment(Integer cocktailId, Integer commentId, Integer userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        
        // 댓글 작성자 확인(userId와 comment.getUser().getId() 비교)
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글 작성자가 아닙니다.");
        }

        comment.setContent(content);
        commentRepository.save(comment); // 수정된 댓글 저장
    }

    @Override
    @Transactional 
    public void deleteComment(Integer cocktailId, Integer commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        // 댓글 작성자 확인
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("댓글 작성자가 아닙니다.");
        }
        
        commentRepository.delete(comment); // 댓글 삭제
    }
}