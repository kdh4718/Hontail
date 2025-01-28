package com.hontail.back.db.repository;

import com.hontail.back.db.entity.Cocktail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CocktailRepository extends JpaRepository<Cocktail, Integer> {
}
