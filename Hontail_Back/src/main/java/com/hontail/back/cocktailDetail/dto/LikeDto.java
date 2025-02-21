package com.hontail.back.cocktailDetail.dto;

import com.hontail.back.db.entity.Like;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikeDto {
    private Integer likeId;
    private Integer cocktailId;
    private Integer userId;

    @Builder
    public LikeDto(Integer likeId, Integer cocktailId, Integer userId) {
        this.likeId = likeId;
        this.cocktailId = cocktailId;
        this.userId = userId;
    }

    public static LikeDto from(Like like) {
        return LikeDto.builder()
                .likeId(like.getId())
                .cocktailId(like.getCocktail().getId())
                .userId(like.getUser().getId())
                .build();
    }
}