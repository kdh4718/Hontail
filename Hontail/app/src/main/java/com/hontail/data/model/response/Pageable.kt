package com.hontail.data.model.response

data class Pageable(
    val pageNumber: Int,
    val pageSize: Int,
    val sort: SortX,
    val offset: Int,
    val paged: Boolean,
    val unpaged: Boolean
)