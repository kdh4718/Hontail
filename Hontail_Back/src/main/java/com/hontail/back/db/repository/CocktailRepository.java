package com.hontail.back.db.repository;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.cocktail.dto.CocktailSummaryResponseDto;
import com.hontail.back.db.entity.Cocktail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CocktailRepository extends JpaRepository<Cocktail, Integer> {
    @Query(value = """
            SELECT
                c.cocktail_id AS id,
                c.cocktail_name AS cocktailName,
                c.image_url AS imageUrl,
                c.likes AS likes,
                c.alcohol_content AS alcoholContent,
                c.base_spirit AS baseSpirit,
                c.created_at AS createdAt,
                COUNT(DISTINCT ci.ingredient_id) AS ingredientCount
            FROM cocktail c
            LEFT JOIN cocktail_ingredients ci ON c.cocktail_id = ci.cocktail_id
            WHERE (:baseSpirit IS NULL OR c.base_spirit = :baseSpirit)
            GROUP BY c.cocktail_id, c.cocktail_name, c.image_url, c.likes, c.alcohol_content, c.base_spirit, c.created_at
            """,
            countQuery = """
        SELECT COUNT(*) FROM cocktail
        """,
            nativeQuery = true)
    Page<CocktailSummaryProjection> findFilteredCocktails(Pageable pageable, @Param("baseSpirit") String baseSpirit);
}
