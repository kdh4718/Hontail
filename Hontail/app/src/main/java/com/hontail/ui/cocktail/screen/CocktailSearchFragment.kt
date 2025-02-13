package com.hontail.ui.cocktail.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.FragmentCocktailSearchBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.cocktail.adapter.CocktailSearchAdapter
import com.hontail.ui.cocktail.viewmodel.CocktailSearchFragmentViewModel
import com.hontail.util.CommonUtils

class CocktailSearchFragment : BaseFragment<FragmentCocktailSearchBinding>(
    FragmentCocktailSearchBinding::bind,
    R.layout.fragment_cocktail_search
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CocktailSearchFragmentViewModel by viewModels()

    private lateinit var cocktailSearchAdapter: CocktailSearchAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initEvent()
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            val recentList = mutableListOf<RecentItem>().apply {
                add(RecentItem("깔루아 밀크"))
                add(RecentItem("에스프레소 마티니"))
                add(RecentItem("몽키 숄더"))
            }

            val cocktailList = mutableListOf<CocktailListResponse>().apply {
                add(
                    CocktailListResponse(
                        1, "깔루아 밀크", "https://cdn.diffords.com/contrib/stock-images/2016/7/30/20168fcf1a85da47c9369831cca42ee82d33.jpg", 1231, 12, "",
                        "2025-01-27 00:13:32", 5, false
                    )
                )
                add(
                    CocktailListResponse(
                        2, "에스프레소 마티니", "https://cdn.diffords.com/contrib/stock-images/2016/7/30/20168fcf1a85da47c9369831cca42ee82d33.jpg", 0, 0, "리큐어",
                        "2025-01-27 00:13:32", 3, true
                    )
                )
            }

            val items2 = mutableListOf<CocktailSearchItem>().apply {
                add(CocktailSearchItem.SearchBar(null))
                add(CocktailSearchItem.Recent(recentList))
            }

            cocktailSearchAdapter = CocktailSearchAdapter(mainActivity, items2)

            recyclerViewCocktailSearch.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCocktailSearch.adapter = cocktailSearchAdapter
        }
    }

    // 이벤트
    private fun initEvent() {
        binding.apply {
            cocktailSearchAdapter.cocktailSearchListener = object : CocktailSearchAdapter.ItemOnClickListener {
                // 취소
                override fun onClickCancel() {
                    parentFragmentManager.popBackStack("CocktailSearchFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }

                override fun onClickSearch(text: String) {
                    viewModel.getCocktailByName(text)
                }

                // 최근 검색 아이템 or 칵테일 아이템으로 상세 화면 가기.
                override fun onClickCocktailItem() {
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
                }
            }
        }
    }
}

sealed class CocktailSearchItem {

    data class SearchBar(val query: String?): CocktailSearchItem()
    data class Recent(val recentList: List<RecentItem>): CocktailSearchItem()
    data class Result(val resultList: List<CocktailListResponse>): CocktailSearchItem()
}

data class RecentItem(val searchName: String)