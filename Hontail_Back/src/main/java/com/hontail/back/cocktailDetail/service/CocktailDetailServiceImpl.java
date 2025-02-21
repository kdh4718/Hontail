package com.hontail.back.cocktailDetail.service;

import com.hontail.back.cocktailDetail.dto.CocktailDetailDto;
import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.repository.CocktailRepository;
import com.hontail.back.db.repository.LikeRepository;
import com.hontail.back.global.exception.CustomException;
import com.hontail.back.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CocktailDetailServiceImpl implements CocktailDetailService {

    private final CocktailRepository cocktailRepository;
    private final LikeRepository likeRepository;

    @Override
    @Transactional(readOnly = true)
    public CocktailDetailDto getCocktailDetail(Integer cocktailId, Integer userId) {
        Cocktail cocktail = cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new CustomException(ErrorCode.COCKTAIL_DETAIL_NOT_FOUND));

        Long likeCount = likeRepository.countByCocktail(cocktail);
        boolean isLiked = false;
        if (userId != null) {
            isLiked = likeRepository.findByCocktailIdAndUserId(cocktailId, userId).isPresent();
        }

        return CocktailDetailDto.from(cocktail, likeCount, isLiked);
    }
}