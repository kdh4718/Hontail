package com.hontail.back.cocktailDetail.service;

public interface LikeService {
    Integer addLike(Integer cocktailId, Integer userId);
    Integer deleteLike(Integer cocktailId, Integer userId);
}