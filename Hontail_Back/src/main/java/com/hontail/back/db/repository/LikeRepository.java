package com.hontail.back.db.repository;

import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    Long countByCocktail(Cocktail cocktail);
}
