package com.hontail.back.cocktail.service;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.cocktail.dto.TopLikedCocktailDto;
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
    public Page<CocktailSummaryDto> getCocktailsByFilter(String orderBy, String direction, String baseSpirit,
                                                         int page, int size, boolean isCustom, Integer userId) {
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
                boolean isLiked = false;
                if (userId != null) {
                    isLiked = likeRepository.findByCocktailIdAndUserId(cocktail.getId(), userId).isPresent();
                }
                return new CocktailSummaryDto(
                        cocktail.getId(),
                        cocktail.getCocktailName(),
                        cocktail.getImageUrl(),
                        likesCnt.intValue(),
                        cocktail.getAlcoholContent(),
                        cocktail.getBaseSpirit(),
                        cocktail.getCreatedAt(),
                        ingredientCnt,
                        isLiked
                );
            });
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_SORT_PARAMETER);
        }
    }

    @Override
    public List<TopLikedCocktailDto> getTopLikedCocktails() {
        List<TopLikedCocktailDto> topCocktails = cocktailRepository.findTopLiked(PageRequest.of(0, 10))
                .stream()
                .map(cocktail -> {
                    Long ingredientCnt = cocktailIngredientRepository.countByCocktail(cocktail);
                    Long likesCnt = likeRepository.countByCocktail(cocktail);
                    return new TopLikedCocktailDto(
                            cocktail.getId(),
                            cocktail.getCocktailName(),
                            cocktail.getImageUrl(),
                            likesCnt.intValue(),
                            cocktail.getAlcoholContent(),
                            cocktail.getBaseSpirit(),
                            cocktail.getCreatedAt(),
                            ingredientCnt,
                            null    // rank는 밑 로직에서
                    );
                })
                .toList();

        if (topCocktails.isEmpty()) {
            throw new CustomException(ErrorCode.COCKTAIL_NOT_FOUND);
        }

        // 순위 설정 (1부터 시작)
        for (int i = 0; i < topCocktails.size(); i++) {
            topCocktails.get(i).setRank(i + 1);
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
                            likesCnt.intValue(),
                            cocktail.getAlcoholContent(),
                            cocktail.getBaseSpirit(),
                            cocktail.getCreatedAt(),
                            ingredientCnt,
                            true  // 사용자가 좋아요한 리스트 조회라서 true
                    );
                })
                .toList();
    }

    @Override
    public Page<CocktailSummaryDto> searchCocktails(String keyword, int page, int size, Integer userId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new CustomException(ErrorCode.SEARCH_KEYWORD_EMPTY);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Cocktail> cocktails = cocktailRepository.searchByNameContaining(keyword.trim(), pageable);

        if (cocktails.isEmpty()) {
            throw new CustomException(ErrorCode.COCKTAIL_NOT_FOUND);
        }

        return cocktails.map(cocktail -> {
            Long ingredientCnt = cocktailIngredientRepository.countByCocktail(cocktail);
            Long likesCnt = likeRepository.countByCocktail(cocktail);
            boolean isLiked = false;
            if (userId != null) {
                isLiked = likeRepository.findByCocktailIdAndUserId(cocktail.getId(), userId).isPresent();
            }
            return new CocktailSummaryDto(
                    cocktail.getId(),
                    cocktail.getCocktailName(),
                    cocktail.getImageUrl(),
                    likesCnt.intValue(),
                    cocktail.getAlcoholContent(),
                    cocktail.getBaseSpirit(),
                    cocktail.getCreatedAt(),
                    ingredientCnt,
                    isLiked
            );
        });
    }
}