package com.hontail.ui.home.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.CocktailTopLikedResponse
import com.hontail.data.model.response.CocktailTopLikedResponseItem
import com.hontail.databinding.FragmentHomeBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.home.adapter.HomeAdapter
import com.hontail.ui.home.viewmodel.HomeFragmentViewModel
import com.hontail.util.CommonUtils

private const val TAG = "HomeFragment_SSAFY"

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: HomeFragmentViewModel by viewModels()
    private lateinit var homeAdapter: HomeAdapter

    private val categoryItems = listOf(
        HomeCategoryItem("진", "허브와 시트러스 향", R.drawable.category_jin),
        HomeCategoryItem("럼", "사탕수수의 달콤함", R.drawable.category_rum),
        HomeCategoryItem("보드카", "중립적이고 깨끗한 맛", R.drawable.category_vodka),
        HomeCategoryItem("위스키", "깊고 진한 풍미", R.drawable.category_whiskey),
        HomeCategoryItem("데킬라", "선명하고 강렬한 맛", R.drawable.category_tequila),
        HomeCategoryItem("리큐어", "과일, 허브, 향신료", R.drawable.category_liqueur),
        HomeCategoryItem("와인", "과일향의 여운", R.drawable.category_wine),
        HomeCategoryItem("브랜디", "부드러운 목넘김", R.drawable.category_brandy),
        HomeCategoryItem("기타", "K-술, 무알콜 등", R.drawable.category_etc),
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUserId(activityViewModel.userId)
        viewModel.getTopTenCocktail()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNav(false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initData()
        initEvent()
    }

    fun initAdapter() {
        binding.apply {
            val items = mutableListOf<HomeItem>().apply {
                add(HomeItem.PictureDescription)
                add(HomeItem.Category(categoryItems))
                add(HomeItem.TopTen(emptyList()))
            }

            homeAdapter = HomeAdapter(requireContext(), items)
            recyclerViewHome.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerViewHome.adapter = homeAdapter
        }
    }

    fun initData() {
        viewModel.topTenCocktailInfo.observe(viewLifecycleOwner) {
            val updatedItems = mutableListOf<HomeItem>().apply {
                add(HomeItem.PictureDescription)
                add(HomeItem.Category(categoryItems))
                add(HomeItem.TopTen(it))
            }

            homeAdapter.updateItems(updatedItems)
        }
    }

    fun initEvent() {
        binding.apply {
            imageViewHomeBell.setOnClickListener {
                mainActivity.changeFragmentWithCallback(
                    CommonUtils.MainFragmentName.ALARM_FRAGMENT
                ) {
                    // 프래그먼트 전환이 완료된 후 네비게이션 바 숨김
                    mainActivity.hideBottomNav(true)
                }
            }

            fabHomeBartender.setOnClickListener {
                mainActivity.changeFragment(
                    CommonUtils.MainFragmentName.BARTENDER_FRAGMENT
                )
            }

            homeAdapter.homeListener = object : HomeAdapter.ItemOnClickListener {
                override fun onClickCategory(name: String) {
                    activityViewModel.setBaseFilter(name)
                    activityViewModel.setBaseFromHome(true)
                    mainActivity.setSelectedBottomNavigation(R.id.navigation_search)
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_LIST_FRAGMENT)
                }

                override fun onClickTopTen(cocktailId: Int) {
                    activityViewModel.setCocktailId(cocktailId)
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
                }
            }
        }
    }
}

sealed class HomeItem {
    object PictureDescription : HomeItem()
    data class Category(val categoryList: List<HomeCategoryItem>) : HomeItem()
    data class TopTen(val topTenList: List<CocktailTopLikedResponseItem>) : HomeItem()
}

data class HomeCategoryItem(
    val name: String,
    val explanation: String,
    val imageRes: Int
)