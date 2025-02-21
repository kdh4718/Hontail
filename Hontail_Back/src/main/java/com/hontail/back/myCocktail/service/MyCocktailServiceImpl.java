package com.hontail.back.myCocktail.service;

import com.hontail.back.db.entity.*;
import com.hontail.back.db.repository.*;
import com.hontail.back.myCocktail.dto.IngredientRequestDto;
import com.hontail.back.myCocktail.dto.MyCocktailRequestDto;
import com.hontail.back.myCocktail.dto.RecipeRequestDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MyCocktailServiceImpl implements MyCocktailService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CocktailRepository cocktailRepository;

    @Autowired
    private CocktailIngredientRepository cocktailIngredientRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Override
    public int createMyCocktail(Integer userId, MyCocktailRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cocktail cocktail = Cocktail.builder()
                .user(user)
                .makerNickname(requestDto.getMakerNickname())
                .cocktailName(requestDto.getCocktailName())
                .cocktailDescription(requestDto.getDescription())
                .alcoholContent(requestDto.getAlcoholContent())
                .imageUrl(requestDto.getImageUrl())
                .isCustom(requestDto.getIsCustom())
                .baseSpirit(requestDto.getBaseSpirit())
                .build();

        Cocktail savedCocktail = cocktailRepository.save(cocktail);

        // 2. 칵테일 재료 저장
        List<CocktailIngredient> cocktailIngredients = requestDto.getIngredients().stream()
                .map(ingredientDto -> {
                    Ingredient ingredient = ingredientRepository.findById(ingredientDto.getIngredientId())
                            .orElseThrow(() -> new RuntimeException("Ingredient not found"));
                    return CocktailIngredient.builder()
                            .cocktail(savedCocktail)
                            .ingredient(ingredient)
                            .ingredientQuantity(ingredientDto.getIngredientQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        cocktailIngredientRepository.saveAll(cocktailIngredients);


        // 3. 레시피 저장
        List<Recipe> recipes = requestDto.getRecipes().stream()
                .map(recipeDto -> Recipe.builder()
                        .cocktail(savedCocktail)
                        .recipeGuide(recipeDto.getRecipeGuide())
                        .sequence(recipeDto.getSequence())
                        .recipeAction(recipeDto.getRecipeAction())
                        .build())
                .collect(Collectors.toList());

        recipeRepository.saveAll(recipes);


        return savedCocktail.getId();

    }

    @Override
    public int deleteMyCocktail(Integer cocktailId) {
        // 삭제할 칵테일을 미리 조회
        Optional<Cocktail> cocktail = cocktailRepository.findById(cocktailId);

        if (cocktail.isPresent()) {
            // 삭제 수행
            cocktailRepository.deleteById(cocktailId);
            return cocktailId; // 삭제된 ID 반환
        } else {
            throw new EntityNotFoundException("Cocktail with ID " + cocktailId + " not found.");
        }
    }

    @Override
    public int updateMyCocktail(Integer cocktailId, MyCocktailRequestDto requestDto) {
        // 1️⃣ 기존 칵테일 조회 (영속성 컨텍스트에서 관리됨)
        Cocktail cocktail = cocktailRepository.findById(cocktailId)
                .orElseThrow(() -> new EntityNotFoundException("Cocktail with ID " + cocktailId + " not found."));

        // 2️⃣ Dirty Checking으로 칵테일 정보 수정
        cocktail.setCocktailName(requestDto.getCocktailName());
        cocktail.setCocktailDescription(requestDto.getDescription());
        cocktail.setAlcoholContent(requestDto.getAlcoholContent());
        cocktail.setImageUrl(requestDto.getImageUrl());
        cocktail.setIsCustom(requestDto.getIsCustom());
        cocktail.setBaseSpirit(requestDto.getBaseSpirit());

        // 3️⃣ Dirty Checking으로 재료 업데이트
        // 기존 재료 맵핑
        Map<Integer, CocktailIngredient> existingIngredientMap = cocktail.getCocktailIngredients()
                .stream()
                .collect(Collectors.toMap(ci -> ci.getIngredient().getId(), ci -> ci));

        for (IngredientRequestDto ingredientDto : requestDto.getIngredients()) {
            if (existingIngredientMap.containsKey(ingredientDto.getIngredientId())) {
                // 기존 재료가 있다면, 재료의 수량만 업데이트 (Dirty Checking)
                existingIngredientMap.get(ingredientDto.getIngredientId()).setIngredientQuantity(ingredientDto.getIngredientQuantity());
            } else {
                // 새로운 재료 추가
                Ingredient ingredient = ingredientRepository.findById(ingredientDto.getIngredientId())
                        .orElseThrow(() -> new RuntimeException("Ingredient not found"));
                cocktail.getCocktailIngredients().add(CocktailIngredient.builder()
                        .cocktail(cocktail)
                        .ingredient(ingredient)
                        .ingredientQuantity(ingredientDto.getIngredientQuantity())
                        .build());
            }
        }

        // 4️⃣ Dirty Checking으로 레시피 업데이트
        // 기존 레시피 맵핑
        Map<Integer, Recipe> existingRecipeMap = cocktail.getRecipes()
                .stream()
                .collect(Collectors.toMap(Recipe::getSequence, r -> r));

        for (RecipeRequestDto recipeDto : requestDto.getRecipes()) {
            if (existingRecipeMap.containsKey(recipeDto.getSequence())) {
                // 기존 레시피가 있다면 내용만 업데이트 (Dirty Checking)
                Recipe recipe = existingRecipeMap.get(recipeDto.getSequence());
                recipe.setRecipeGuide(recipeDto.getRecipeGuide());
                recipe.setRecipeAction(recipeDto.getRecipeAction());
            } else {
                // 새로운 레시피 추가
                cocktail.getRecipes().add(Recipe.builder()
                        .cocktail(cocktail)
                        .recipeGuide(recipeDto.getRecipeGuide())
                        .sequence(recipeDto.getSequence())
                        .recipeAction(recipeDto.getRecipeAction())
                        .build());
            }
        }

        // 5️⃣ Dirty Checking으로 자동 저장됨 (트랜잭션 종료 시 flush)
        return cocktail.getId();
    }
}
