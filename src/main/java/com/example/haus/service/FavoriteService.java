package com.example.haus.service;

import com.example.haus.domain.dto.pagination.PaginationRequestDto;
import com.example.haus.domain.dto.pagination.PaginationResponseDto;
import com.example.haus.domain.dto.request.product.AddFavoriteRequestDto;
import com.example.haus.domain.dto.response.product.CheckFavoriteResponseDto;
import com.example.haus.domain.dto.response.product.FavoriteResponseDto;


public interface FavoriteService {

    FavoriteResponseDto addFavorite(String userId, AddFavoriteRequestDto request);

    void removeFavorite(String userId, Long productId);

    PaginationResponseDto<FavoriteResponseDto> getFavorites(
        String userId, 
        PaginationRequestDto paginationRequest
    );

    CheckFavoriteResponseDto checkFavorite(String userId, Long productId);

    long countFavorites(String userId);
}
