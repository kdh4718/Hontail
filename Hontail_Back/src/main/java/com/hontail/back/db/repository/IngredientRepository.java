package com.hontail.back.db.repository;

import com.hontail.back.db.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Integer>, CocktailCustomRepository {

}
