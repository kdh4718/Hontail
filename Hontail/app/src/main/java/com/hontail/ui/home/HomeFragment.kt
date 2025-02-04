package com.hontail.ui.home

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentHomeBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.util.CommonUtils
import com.hontail.util.PermissionChecker

private const val TAG = "HomeFragment_SSAFY"

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var homeAdapter: HomeAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    fun initAdapter() {
        binding.apply {
            val categoryItems = listOf(
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

            val topTenItems = listOf(
                HomeTopTenItem(1, "네그로니\n스발리아토", R.drawable.topten_sample),
                HomeTopTenItem(2, "마가리타", R.drawable.topten_sample),
                HomeTopTenItem(3, "모히또", R.drawable.topten_sample)
            )

            val items = mutableListOf<HomeItem>().apply {
                add(HomeItem.PictureDescription)
                add(HomeItem.Category(categoryItems))
                add(HomeItem.TopTen(topTenItems))
            }

            homeAdapter = HomeAdapter(requireContext(), items)
            recyclerViewHome.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            recyclerViewHome.adapter = homeAdapter
        }

    }
}

sealed class HomeItem {
    object PictureDescription : HomeItem()
    data class Category(val categoryList: List<HomeCategoryItem>) : HomeItem()
    data class TopTen(val topTenList: List<HomeTopTenItem>) : HomeItem()
}

data class HomeTopTenItem(
    val rank: Int,        // 순위
    val name: String,     // 칵테일 이름
    val imageRes: Int     // 이미지 리소스 ID
)

data class HomeCategoryItem(
    val name: String,
    val explanation: String,
    val imageRes: Int
)

