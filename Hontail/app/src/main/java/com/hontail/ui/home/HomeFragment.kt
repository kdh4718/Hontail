package com.hontail.ui.home

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentHomeBinding
import com.hontail.databinding.ListItemHomeToptenBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.util.CommonUtils

private const val TAG = "HomeFragment_SSAFY"

class HomeFragment : BaseFragment<FragmentHomeBinding>(
    FragmentHomeBinding::bind,
    R.layout.fragment_home
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initEvent()
    }

    fun initAdapter() {
        val recyclerView = binding.recyclerViewHomeTopTen

        val items = listOf(
            HomeTopTenItem(1, "네그로니\n스발리아토", R.drawable.topten_sample),
            HomeTopTenItem(2, "마가리타", R.drawable.topten_sample),
            HomeTopTenItem(3, "모히또", R.drawable.topten_sample),
            HomeTopTenItem(4, "다이퀴리", R.drawable.topten_sample),
            HomeTopTenItem(5, "마티니", R.drawable.topten_sample),
            HomeTopTenItem(6, "올드 패션드", R.drawable.topten_sample),
            HomeTopTenItem(7, "롱 아일랜드 아이스티", R.drawable.topten_sample),
            HomeTopTenItem(8, "진토닉", R.drawable.topten_sample),
            HomeTopTenItem(9, "위스키 사워", R.drawable.topten_sample),
            HomeTopTenItem(10, "블러디 메리", R.drawable.topten_sample)
        )

        // 초기 스크롤 위치를 리스트 중앙으로 설정
        recyclerView.layoutManager = SlowScrollLinearLayoutManager(requireContext())
        recyclerView.adapter = TopTenAdapter(requireContext(), items)

        val startPosition = items.size * 100 // 시작 위치를 중간으로 설정
        recyclerView.scrollToPosition(startPosition)

        recyclerView.addOnScrollListener(CenterScrollListener()) // 중앙 확대 효과 적용

        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerView)
    }

    fun initEvent(){
        binding.apply {
            imageViewHomePicture.setOnClickListener {
                mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_TAKE_PICTURE_FRAGMENT)
            }
        }
    }
}

data class HomeTopTenItem(
    val rank: Int,        // 순위
    val name: String,     // 칵테일 이름
    val imageRes: Int     // 이미지 리소스 ID
)


