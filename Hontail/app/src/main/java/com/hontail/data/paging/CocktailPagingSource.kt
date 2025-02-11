package com.hontail.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.remote.RetrofitUtil

class CocktailPagingSource(
    private val orderBy: String,
    private val direction: String,
    private val baseSpirit: String,
    private val isCustom: Boolean
) : PagingSource<Int, CocktailListResponse>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CocktailListResponse> {
        return try {
            val page = params.key ?: 0 // 첫 페이지는 0
            val size = params.loadSize // 요청할 데이터 크기

            val response = RetrofitUtil.cocktailService.getCocktailFiltering(orderBy, direction, baseSpirit, page, size, isCustom)

            LoadResult.Page(
                data = response.content,
                prevKey = if (page == 0) null else page - 1,  // 이전 페이지 없음이면 null
                nextKey = if (response.content.isEmpty()) null else page + 1 // 다음 페이지 없으면 null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, CocktailListResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
