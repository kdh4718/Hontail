package com.hontail.back.cocktail.service;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.repository.CocktailIngredientRepository;
import com.hontail.back.db.repository.CocktailRepository;
import com.hontail.back.db.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CocktailServiceImpl implements CocktailService {

    @Autowired
    private CocktailRepository cocktailRepository;

    @Autowired
    private CocktailIngredientRepository cocktailIngredientRepository;
    @Autowired
    private LikeRepository likeRepository;

    @Override
    public Page<CocktailSummaryDto> getCocktailsByFilter(String orderBy, String direction, String baseSpirit, int page, int size) {

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        Sort sort = Sort.by(sortDirection, orderBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Cocktail> cocktails;


        if (baseSpirit == null || baseSpirit.isEmpty()) {
            cocktails = cocktailRepository.findAll(pageable);
        } else {
            cocktails = cocktailRepository.findByBaseSpirit(baseSpirit, pageable);
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

    }
}
