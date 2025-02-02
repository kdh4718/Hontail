package com.hontail.back.cocktailDetail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeDto {
    private Integer likeId;
    private Integer cocktailId;
    private Integer userId;
}