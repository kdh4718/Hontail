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
import java.util.Optional;


public interface CocktailRepository extends JpaRepository<Cocktail, Integer>, CocktailCustomRepository {
    Page<Cocktail> findByBaseSpirit(String baseSpirit, Pageable pageable);

    // 기본/커스텀 전체 조회
    Page<Cocktail> findByIsCustom(Byte isCustom, Pageable pageable);
    // 기본/커스텀 필터링 (베이스 스피릿 포함)
    Page<Cocktail> findByIsCustomAndBaseSpirit(Byte isCustom, String baseSpirit, Pageable pageable);

    // 좋아요 상위 10개 칵테일 조회를 위한 쿼리
    @Query("SELECT c FROM Cocktail c LEFT JOIN c.likes l " +
            "GROUP BY c.id, c.cocktailName, c.imageUrl, c.alcoholContent, c.baseSpirit, c.createdAt " +
            "ORDER BY COUNT(l) DESC, c.id ASC")
    List<Cocktail> findTopLiked(Pageable pageable);

    // Bartender 랜덤 칵테일 추천을 위한 쿼리
    @Query(value = "SELECT * FROM cocktail ORDER BY RAND() LIMIT 1",
            nativeQuery = true)
    Optional<Cocktail> findRandomCocktail();

    // 칵테일 이름으로 검색 (대소문자 구분 없이 + 일정 부분만 같은 결과도 포함)
    @Query("SELECT c FROM Cocktail c WHERE LOWER(c.cocktailName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Cocktail> searchByNameContaining(@Param("keyword") String keyword, Pageable pageable);
}
