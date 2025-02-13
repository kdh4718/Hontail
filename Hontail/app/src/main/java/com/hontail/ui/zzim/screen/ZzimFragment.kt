package com.hontail.ui.zzim.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.data.model.response.LikedCocktail
import com.hontail.data.model.response.RecentViewedCocktail
import com.hontail.databinding.FragmentZzimBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.zzim.adapter.ZzimAdapter
import com.hontail.ui.zzim.viewmodel.ZzimViewModel

class ZzimFragment: BaseFragment<FragmentZzimBinding>(
    FragmentZzimBinding::bind,
    R.layout.fragment_zzim
) {
    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: ZzimViewModel by viewModels()

    private lateinit var zzimAdapter: ZzimAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    // RecyclerView Adapter 연결
    private fun initAdapter() {

        binding.apply {

            // 찜 리스트 X, 최근 본 상품 X
            val items = mutableListOf<ZzimItem>(
                ZzimItem.Empty
            )

            // 찜 리스트 X, 최근 본 상품 O
            val items2 = mutableListOf<ZzimItem>(
                ZzimItem.Empty,
                ZzimItem.RecentViewedList(
                    listOf(
                        CocktailListResponse(1, "Mojito", "image_url", 150, 40, "Rum", "2024-02-10", 5, false),
                        CocktailListResponse(1, "Mojito", "image_url", 150, 40, "Rum", "2024-02-10", 5, false),
                        CocktailListResponse(1, "Mojito", "image_url", 150, 40, "Rum", "2024-02-10", 5, false),
                        CocktailListResponse(1, "Mojito", "image_url", 150, 40, "Rum", "2024-02-10", 5, false),
                    )
                )
            )

            val items3 = mutableListOf<ZzimItem>(
                ZzimItem.LikedList(
                    listOf(
                        CocktailListResponse(4, "Margarita", "image_url_4", 300, 38, "Tequila", "2024-02-09", 6, true),
                        CocktailListResponse(5, "Negroni", "image_url_5", 250, 39, "Gin", "2024-02-08", 4, true),
                        CocktailListResponse(6, "Daiquiri", "image_url_6", 220, 37, "Rum", "2024-02-07", 5, true),
                        CocktailListResponse(7, "Cosmopolitan", "image_url_7", 280, 35, "Vodka", "2024-02-06", 5, true)
                    )
                ),
                ZzimItem.RecentViewedList(
                    listOf(
                        CocktailListResponse(8, "Whiskey Sour", "image_url_8", 190, 41, "Whiskey", "2024-02-05", 3, false),
                        CocktailListResponse(9, "Pina Colada", "image_url_9", 210, 34, "Rum", "2024-02-04", 6, false)
                    )
                )
            )

            zzimAdapter = ZzimAdapter(mainActivity, items3)

            recyclerViewZzim.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewZzim.adapter = zzimAdapter
        }
    }
}

sealed class ZzimItem {
    data class LikedList(val likedList: List<CocktailListResponse>): ZzimItem()
    data class RecentViewedList(val recentList: List<CocktailListResponse>): ZzimItem()
    object Empty: ZzimItem()
}