package com.hontail.back.db.repository;

import com.hontail.back.cocktail.dto.CocktailSummaryDto;
import com.hontail.back.cocktail.dto.CocktailSummaryResponseDto;
import com.hontail.back.db.entity.Cocktail;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CocktailRepository extends JpaRepository<Cocktail, Integer> {
    Page<Cocktail> findByBaseSpirit(String baseSpirit, Pageable pageable);
}
