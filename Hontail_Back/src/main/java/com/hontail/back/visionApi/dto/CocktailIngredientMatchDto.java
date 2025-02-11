package com.hontail.back.visionApi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CocktailIngredientMatchDto {
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
    private Long missingIngredients;

    @Override
    public String toString() {
        return "CocktailIngredientMatchDto{" +
                "id=" + id +
                ", cocktailName='" + cocktailName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", likes=" + likes +
                ", alcoholContent=" + alcoholContent +
                ", baseSpirit='" + baseSpirit + '\'' +
                ", createdAt=" + createdAt +
                ", ingredientCount=" + ingredientCount +
                ", isLiked=" + isLiked +
                ", missingIngredients=" + missingIngredients +
                '}';
    }
}
