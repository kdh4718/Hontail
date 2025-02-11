package com.hontail.ui.cocktail.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.FragmentCocktailListBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.cocktail.viewmodel.CocktailListFragmentViewModel
import com.hontail.ui.cocktail.adapter.CocktailListAdapter
import com.hontail.ui.picture.FilterBottomSheetFragment
import com.hontail.util.CommonUtils

class CocktailListFragment : BaseFragment<FragmentCocktailListBinding>(
    FragmentCocktailListBinding::bind,
    R.layout.fragment_cocktail_list
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CocktailListFragmentViewModel by viewModels()
    private val filters = mutableListOf<String>().apply {
        add("찜")
        add("시간")
        add("도수")
        add("베이스주")
    }

    private lateinit var cocktailListAdapter: CocktailListAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.baseSpirit = activityViewModel.baseSpirit.value!!
        viewModel.setUserId(activityViewModel.userId)
        viewModel.getCocktailFiltering()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(false)  // 하단바 다시 보이게 설정
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNav(false)  // 하단바 다시 보이게 설정
        initAdapter()
        initData()
        initEvent()
    }

    // 어댑터 연결
    private fun initAdapter() {
        binding.apply {
            val items = mutableListOf<CocktailListItem>().apply {
                add(CocktailListItem.SearchBar)
                add(CocktailListItem.TabLayout)
                add(CocktailListItem.Filter(filters))
                add(CocktailListItem.CocktailItems(emptyList()))
            }

            cocktailListAdapter = CocktailListAdapter(mainActivity, items)

            recyclerViewCocktailList.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCocktailList.adapter = cocktailListAdapter
        }
    }

    fun initData() {
        viewModel.cocktailList.observe(viewLifecycleOwner) { cocktailList ->
            cocktailList?.let {
                val updatedItems = mutableListOf<CocktailListItem>().apply {
                    add(CocktailListItem.SearchBar)
                    add(CocktailListItem.TabLayout)
                    add(CocktailListItem.Filter(filters))
                    add(CocktailListItem.CocktailItems(it))
                }

                cocktailListAdapter.updateItems(updatedItems)  // 어댑터 데이터 변경
            }
        }
    }

    // 이벤트 처리
    fun initEvent() {
        binding.apply {
            cocktailListAdapter.cocktailListListener = object : CocktailListAdapter.ItemOnClickListener {

                // 랜덤 다이얼로그 띄우기.
                override fun onClickRandom() {
                    val dialog = CocktailRandomDialogFragment()
                    dialog.show(parentFragmentManager, "CocktailRandomDialog")
                }

                // 칵테일 상세 화면으로 가기.
                override fun onClickCocktailItem(cocktailId: Int) {
                    activityViewModel.setCocktailId(cocktailId)
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
                }

                // 칵테일 검색 화면 가기.
                override fun onClickSearch() {
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_SEARCH_FRAGMENT)
                }

                // 탭 눌렀을 때
                override fun onClickTab(position: Int) {
                    when (position) {
                        // 디폴트 칵테일
                        0 -> {
                            viewModel.isCustom = false
                            viewModel.getCocktailFiltering()
                        }
                        // 커스텀 칵테일
                        1 -> {
                            viewModel.isCustom = true
                            viewModel.getCocktailFiltering()
                        }

                        else -> {}
                    }
                }

                // 필터 눌렀을 때
                override fun onClickFilter(position: Int) {
                    val bottomSheetFragment = FilterBottomSheetFragment.newInstance(position)
                    bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
                }
            }
        }
    }
}

sealed class CocktailListItem {
    object SearchBar : CocktailListItem()
    object TabLayout : CocktailListItem()
    data class Filter(val filters: List<String>) : CocktailListItem()
    data class CocktailItems(val cocktails: List<CocktailListResponse>) : CocktailListItem()
}