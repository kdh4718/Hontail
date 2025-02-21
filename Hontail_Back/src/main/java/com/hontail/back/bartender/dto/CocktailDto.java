package com.hontail.back.bartender.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CocktailDto {
    private Integer id;
    private String name;
    private String description;
    private String imageUrl;
    private int alcoholContent;
}