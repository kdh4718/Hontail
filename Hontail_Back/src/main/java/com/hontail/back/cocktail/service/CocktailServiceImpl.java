package com.hontail.back.cocktail.service;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.repository.CocktailIngredientRepository;
import com.hontail.back.db.repository.CocktailRepository;
import com.hontail.back.db.repository.LikeRepository;
import com.hontail.back.db.repository.UserRepository;
import com.hontail.back.global.exception.CustomException;
import com.hontail.back.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CocktailServiceImpl implements CocktailService {

    private final CocktailRepository cocktailRepository;
    private final CocktailIngredientRepository cocktailIngredientRepository;
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;

    @Override
    public Page<CocktailSummaryDto> getCocktailsByFilter(String orderBy, String direction, String baseSpirit, int page, int size, boolean isCustom) {
        try {
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(sortDirection, orderBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Byte isCustomByte = isCustom ? (byte) 1 : (byte) 0;

            Page<Cocktail> cocktails;
            if (baseSpirit == null || baseSpirit.isEmpty()) {
                cocktails = cocktailRepository.findByIsCustom(isCustomByte, pageable);
            } else {
                cocktails = cocktailRepository.findByIsCustomAndBaseSpirit(isCustomByte, baseSpirit, pageable);
            }

            if (cocktails.isEmpty()) {
                throw new CustomException(ErrorCode.COCKTAIL_NOT_FOUND);
            }

            return cocktails.map(cocktail -> {
                Long ingredientCnt = cocktailIngredientRepository.countByCocktail(cocktail);
                Long likesCnt = likeRepository.countByCocktail(cocktail);
                return new CocktailSummaryDto(
                        cocktail.getId(),
                        cocktail.getCocktailName(),
                        cocktail.getImageUrl(),
                        likesCnt,
                        cocktail.getAlcoholContent(),
                        cocktail.getBaseSpirit(),
                        cocktail.getCreatedAt(),
                        ingredientCnt
                );
            });
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_SORT_PARAMETER);
        }
    }

    @Override
    public List<CocktailSummaryDto> getTopLikedCocktails() {
        List<CocktailSummaryDto> topCocktails = cocktailRepository.findTopLiked(PageRequest.of(0, 10))
                .stream()
                .map(cocktail -> {
                    Long ingredientCnt = cocktailIngredientRepository.countByCocktail(cocktail);
                    Long likesCnt = likeRepository.countByCocktail(cocktail);
                    return new CocktailSummaryDto(
                            cocktail.getId(),
                            cocktail.getCocktailName(),
                            cocktail.getImageUrl(),
                            likesCnt,
                            cocktail.getAlcoholContent(),
                            cocktail.getBaseSpirit(),
                            cocktail.getCreatedAt(),
                            ingredientCnt
                    );
                })
                .toList();

        if (topCocktails.isEmpty()) {
            throw new CustomException(ErrorCode.COCKTAIL_NOT_FOUND);
        }

        return topCocktails;
    }

    @Override
    public List<CocktailSummaryDto> getLikedCocktails(Integer userId) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        List<Cocktail> likedCocktails = likeRepository.findCocktailsByUserId(userId);

        if (likedCocktails.isEmpty()) {
            throw new CustomException(ErrorCode.COCKTAIL_NOT_FOUND);
        }

        return likedCocktails.stream()
                .map(cocktail -> {
                    Long ingredientCnt = cocktailIngredientRepository.countByCocktail(cocktail);
                    Long likesCnt = likeRepository.countByCocktail(cocktail);
                    return new CocktailSummaryDto(
                            cocktail.getId(),
                            cocktail.getCocktailName(),
                            cocktail.getImageUrl(),
                            likesCnt,
                            cocktail.getAlcoholContent(),
                            cocktail.getBaseSpirit(),
                            cocktail.getCreatedAt(),
                            ingredientCnt
                    );
                })
                .toList();
    }
}