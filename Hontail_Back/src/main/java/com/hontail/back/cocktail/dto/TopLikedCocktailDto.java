package com.hontail.back.cocktail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TopLikedCocktailDto {
    private Integer id;
    private String cocktailName;
    private String imageUrl;
    private Integer likesCnt;
    private Integer alcoholContent;
    private String baseSpirit;
    private LocalDateTime createdAt;
    private Long ingredientCount;
    private Integer rank;
}