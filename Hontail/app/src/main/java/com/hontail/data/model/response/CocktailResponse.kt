package com.hontail.data.model.response

data class CocktailResponse(
    val content: List<CocktailListResponse>,
    val pageable: Pageable,
    val totalPages: Int,
    val totalElements: Int,
    val last: Boolean,
    val size: Int,
    val number: Int,
    val sort: SortX,
    val numberOfElements: Int,
    val first: Boolean,
    val empty: Boolean
)
