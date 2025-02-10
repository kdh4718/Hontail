package com.hontail.back.cocktail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CocktailSummaryDto {
    private Integer id;
    private String cocktailName;
    private String imageUrl;
    private Long likes;
    private Integer alcoholContent;
    private String baseSpirit;
    private LocalDateTime createdAt;
    private Long ingredientCount;
    @JsonProperty("isLiked")
    private boolean isLiked;
}

