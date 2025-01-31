package com.hontail.back.cocktailDetail.service;

import com.hontail.back.cocktailDetail.dto.CocktailDetailDto;

public interface CocktailDetailService {
    CocktailDetailDto getCocktailDetail(Integer cocktailId);
}