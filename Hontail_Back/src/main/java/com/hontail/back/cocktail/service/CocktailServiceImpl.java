package com.hontail.back.cocktail.service;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.db.repository.CocktailRepository;
import com.hontail.back.db.repository.CocktailSummaryProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CocktailServiceImpl implements CocktailService {

    @Autowired
    private CocktailRepository cocktailRepository;


    @Override
    public Page<CocktailSummaryDto> getFilteredCocktails(int page, int size, String baseSpirit, String sortBy, String sortDirection) {

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return cocktailRepository.findFilteredCocktails(pageable, baseSpirit).
                map(p ->
                        new CocktailSummaryDto(p.getId(),
                                p.getCocktailName(),
                                p.getImageUrl(),
                                p.getLikes(),
                                p.getAlcoholContent(),
                                p.getBaseSpirit(),
                                p.getCreatedAt(),
                                p.getIngredientCount()));
    }
}
