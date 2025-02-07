package com.hontail.back.myCocktail.service;

import com.hontail.back.db.entity.*;
import com.hontail.back.db.repository.*;
import com.hontail.back.myCocktail.dto.IngredientRequestDto;
import com.hontail.back.myCocktail.dto.MyCocktailRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
}
