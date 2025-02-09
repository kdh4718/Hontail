package com.hontail.back.db.repository;

import com.hontail.back.db.entity.Cocktail;
import com.hontail.back.db.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {

    Long countByCocktail(Cocktail cocktail);
    Optional<Like> findByCocktailIdAndUserId(Integer cocktailId, Integer userId);

    // 사용자가 좋아요한 칵테일 목록 조회
    @Query("SELECT l.cocktail FROM Like l WHERE l.user.id = :userId")
    List<Cocktail> findCocktailsByUserId(@Param("userId") Integer userId);
}

