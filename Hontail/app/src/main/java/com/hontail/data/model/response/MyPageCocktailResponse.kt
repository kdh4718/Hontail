package com.hontail.data.model.response

data class MyPageCocktailResponse(
    val content: List<ContentX>,
    val empty: Boolean,
    val first: Boolean,
    val last: Boolean,
    val number: Int,
    val numberOfElements: Int,
    val pageable: PageableX,
    val size: Int,
    val sort: SortXX,
    val totalElements: Int,
    val totalPages: Int
)