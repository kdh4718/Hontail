package com.hontail.back.ingredient.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientSearchDto {
    private Integer id;
    private String cocktailName;
    private String imageUrl;
    private Long likes;
    private Integer alcoholContent;
    private String baseSpirit;
    private LocalDateTime createdAt;
    private Long ingredientCount;
}
