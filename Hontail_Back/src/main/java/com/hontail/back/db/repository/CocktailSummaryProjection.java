package com.hontail.back.db.repository;

import java.time.LocalDateTime;

public interface CocktailSummaryProjection {
    Integer getId();
    String getCocktailName();
    String getImageUrl();
    Integer getLikes();
    Integer getAlcoholContent();
    String getBaseSpirit();
    LocalDateTime getCreatedAt();
    Long getIngredientCount();
}
