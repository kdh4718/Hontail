package com.hontail.back.db.repository;

import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    Long countByCocktail(Cocktail cocktail);
    Optional<Like> findByCocktailIdAndUserId(Integer cocktailId, Integer userId);
}

