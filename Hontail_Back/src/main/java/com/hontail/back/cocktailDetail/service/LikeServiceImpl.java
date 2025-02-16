package com.hontail.back.cocktailDetail.service;

import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.entity.Like;
import com.hontail.back.db.entity.User;
import com.hontail.back.db.repository.CocktailRepository;
import com.hontail.back.db.repository.LikeRepository;
import com.hontail.back.db.repository.UserRepository;
import com.hontail.back.global.exception.CustomException;
import com.hontail.back.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final CocktailRepository cocktailRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    @Override
    @Transactional
    public Integer addLike(Integer cocktailId, Integer userId) {
        Cocktail cocktail = cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new CustomException(ErrorCode.COCKTAIL_DETAIL_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (likeRepository.findByCocktailIdAndUserId(cocktailId, userId).isPresent()) {
            throw new CustomException(ErrorCode.LIKE_ALREADY_EXISTS);
        }

        Like like = new Like();
        like.setCocktail(cocktail);
        like.setUser(user);
        likeRepository.save(like);

        cocktail.increaseLikes();

        return likeRepository.countByCocktail(cocktail).intValue();
    }

    @Override
    @Transactional
    public Integer deleteLike(Integer cocktailId, Integer userId) {
        Cocktail cocktail = cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new CustomException(ErrorCode.COCKTAIL_DETAIL_NOT_FOUND));

        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Like like = likeRepository.findByCocktailIdAndUserId(cocktailId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.LIKE_NOT_FOUND));

        likeRepository.delete(like);

        cocktail.decreaseLikes();

        return likeRepository.countByCocktail(cocktail).intValue();
    }
}