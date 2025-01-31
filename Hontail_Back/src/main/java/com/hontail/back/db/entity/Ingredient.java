package com.hontail.back.db.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "ingredients")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id", nullable = false)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @Column(name = "ingredient_name", nullable = false, length = 100)
    private String ingredientName;

    @Size(max = 100)
    @NotNull
    @Column(name = "ingredient_name_kor", nullable = false, length = 100)
    private String ingredientNameKor;

    @Size(max = 100)
    @Column(name = "ingredient_category", length = 100)
    private String ingredientCategory;

    @Size(max = 100)
    @Column(name = "ingredient_category_kor", length = 100)
    private String ingredientCategoryKor;

    @Size(max = 50)
    @Column(name = "ingredient_type", length = 50)
    private String ingredientType;

    @Column(name = "ingredient_alcohol_content")
    private Integer ingredientAlcoholContent;

    @Lob
    @Column(name = "ingredient_image")
    private String ingredientImage;

    @OneToMany(mappedBy = "ingredient")
    private List<CocktailIngredient> cocktailIngredients = new ArrayList<>();

}