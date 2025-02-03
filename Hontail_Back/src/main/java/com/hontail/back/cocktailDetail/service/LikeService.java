package com.hontail.back.cocktailDetail.service;

public interface LikeService {
    void addLike(Integer cocktailId, Integer userId);
    void deleteLike(Integer cocktailId, Integer userId);
}