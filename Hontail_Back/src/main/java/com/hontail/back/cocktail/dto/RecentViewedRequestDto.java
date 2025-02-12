package com.hontail.back.cocktail.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RecentViewedRequestDto {
    private List<Integer> cocktailIds;
}