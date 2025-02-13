package com.hontail.back.db.repository;

import java.util.List;

public interface IngredientRepositoryCustom {
    List<Integer> findByIngredientNames(List<String> ingredientNames);
}
