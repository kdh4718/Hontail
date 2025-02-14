package com.hontail.back.myCocktail.service;

import com.hontail.back.myCocktail.dto.MyCocktailRequestDto;

public interface MyCocktailService {
    public int createMyCocktail(Integer userId, MyCocktailRequestDto requestDto);

    public int deleteMyCocktail(Integer cocktailId);

    public int updateMyCocktail(Integer cocktailId, MyCocktailRequestDto requestDto);
}
