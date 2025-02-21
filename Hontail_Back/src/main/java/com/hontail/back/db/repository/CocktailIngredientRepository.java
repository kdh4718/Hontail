package com.hontail.back.db.repository;

import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.entity.CocktailIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CocktailIngredientRepository extends JpaRepository<CocktailIngredient, Integer> {
    Long countByCocktail(Cocktail cocktail);
}
