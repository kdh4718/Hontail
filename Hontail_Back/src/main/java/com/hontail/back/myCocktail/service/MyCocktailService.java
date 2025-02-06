package com.hontail.back.myCocktail.service;

import com.hontail.back.myCocktail.dto.MyCocktailRequestDto;

public interface MyCocktailService {
    public int createMyCocktail(Integer userId, MyCocktailRequestDto requestDto);
}
