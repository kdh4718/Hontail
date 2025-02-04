package com.hontail.back.cocktailDetail.service;

import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.entity.Like;
import com.hontail.back.db.entity.User;
import com.hontail.back.db.repository.CocktailRepository;
import com.hontail.back.db.repository.LikeRepository;
import com.hontail.back.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private CocktailRepository cocktailRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Override
    @Transactional
    public void addLike(Integer cocktailId, Integer userId) {
        // 칵테일 존재 확인
        Cocktail cocktail = cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new RuntimeException("칵테일을 찾을 수 없습니다."));

        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 이미 좋아요가 있는지 확인
        if (likeRepository.findByCocktailIdAndUserId(cocktailId, userId).isPresent()) {
            throw new RuntimeException("이미 좋아요를 누른 칵테일입니다.");
        }

        Like like = new Like();
        like.setCocktail(cocktail);
        like.setUser(user);

        likeRepository.save(like);
    }

    @Override
    @Transactional
    public void deleteLike(Integer cocktailId, Integer userId) {
        // 칵테일 존재 확인
        Cocktail cocktail = cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new RuntimeException("칵테일을 찾을 수 없습니다."));

        // 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 좋아요 존재 확인
        Like like = likeRepository.findByCocktailIdAndUserId(cocktailId, userId)
                .orElseThrow(() -> new RuntimeException("좋아요를 찾을 수 없습니다."));

        likeRepository.delete(like);
    }
}