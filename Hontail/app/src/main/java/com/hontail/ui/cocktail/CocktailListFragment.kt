package com.hontail.ui.cocktail

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentBatenderBinding
import com.hontail.databinding.FragmentCocktailListBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.mypage.Cocktail
import com.hontail.ui.picture.FilterBottomSheetFragment
import com.hontail.util.CommonUtils

class CocktailListFragment : BaseFragment<FragmentCocktailListBinding>(
    FragmentCocktailListBinding::bind,
    R.layout.fragment_cocktail_list
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var cocktailListAdapter: CocktailListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initEvent()
    }

    // 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            val filters = mutableListOf<String>().apply {
                add("찜")
                add("시간")
                add("도수")
                add("베이스주")
            }

            val cocktails = mutableListOf<Cocktail>().apply {
                add(Cocktail("깔루아 밀크", "리큐어", 2, 1231, 5))
                add(Cocktail("에스프레소 마티니", "리큐어", 3, 1231, 22))
                add(Cocktail("깔루아 콜라", "리큐어", 2, 1231, 16))
                add(Cocktail("B-52", "리큐어", 5, 1231, 26))
            }

            val items = mutableListOf<CocktailListItem>().apply {
                add(CocktailListItem.SearchBar)
                add(CocktailListItem.TabLayout)
                add(CocktailListItem.Filter(filters))
                add(CocktailListItem.CocktailItems(cocktails))
            }

            cocktailListAdapter = CocktailListAdapter(mainActivity, items)

            recyclerViewCocktailList.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCocktailList.adapter = cocktailListAdapter
        }
    }

    // 이벤트 처리
    fun initEvent(){
        binding.apply {

            cocktailListAdapter.cocktailListListener = object : CocktailListAdapter.ItemOnClickListener {

                // 랜덤 다이얼로그 띄우기.
                override fun onClickRandom() {
                    TODO("Not yet implemented")
                }

                // 칵테일 상세 화면으로 가기.
                override fun onClickCocktailItem() {
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
                }

                // 칵테일 검색 화면 가기.
                override fun onClickSearch() {
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_SEARCH_FRAGMENT)
                }

                // 탭 눌렀을 때
                override fun onClickTab(position: Int) {
                    when(position) {

                        // 디폴트 칵테일
                        0 -> { }

                        // 커스텀 칵테일
                        1 -> { }

                        else -> { }
                    }
                }

                // 필터 눌렀을 때
                override fun onClickFilter() {
                    TODO("Not yet implemented")
                }
            }
        }
    }
}

sealed class CocktailListItem {

    object SearchBar: CocktailListItem()
    object TabLayout: CocktailListItem()
    data class Filter(val filters: List<String>): CocktailListItem()
    data class CocktailItems(val cocktails: List<Cocktail>): CocktailListItem()
}