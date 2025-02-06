package com.hontail.ui.cocktail

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCocktailSearchBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.mypage.Cocktail
import com.hontail.util.CommonUtils

class CocktailSearchFragment : BaseFragment<FragmentCocktailSearchBinding>(
    FragmentCocktailSearchBinding::bind,
    R.layout.fragment_cocktail_search
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

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

            val cocktailList = mutableListOf<Cocktail>().apply {
                add(Cocktail("깔루아 밀크", "리큐어", 2, 1231, 5))
                add(Cocktail("에스프레소 마티니", "리큐어", 3, 1231, 22))
                add(Cocktail("깔루아 콜라", "리큐어", 2, 1231, 16))
                add(Cocktail("B-52", "리큐어", 5, 1231, 26))
            }

            val items = mutableListOf<CocktailSearchItem>().apply {
                add(CocktailSearchItem.SearchBar(null))
                add(CocktailSearchItem.Result(cocktailList))
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

            // 취소
            cocktailSearchAdapter.cocktailSearchListener = object : CocktailSearchAdapter.ItemOnClickListener {
                override fun onClickCancel() {
                    parentFragmentManager.popBackStack("CocktailSearchFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
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
    data class Result(val resultList: List<Cocktail>): CocktailSearchItem()
}

data class RecentItem(val cocktailName: String)