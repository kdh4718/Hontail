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

    private lateinit var topTenAdapter: TopTenAdapter
    private lateinit var baseCategoryAdapter: BaseCategoryAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initText()
        initEvent()
    }

    fun initAdapter() {
        val recyclerViewTopTen = binding.recyclerViewHomeTopTen

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
        recyclerViewTopTen.layoutManager = SlowScrollLinearLayoutManager(requireContext())
        topTenAdapter = TopTenAdapter(requireContext(), items)
        recyclerViewTopTen.adapter = topTenAdapter

        val startPosition = items.size * 100 // 시작 위치를 중간으로 설정
        recyclerViewTopTen.scrollToPosition(startPosition)

        recyclerViewTopTen.addOnScrollListener(CenterScrollListener()) // 중앙 확대 효과 적용

        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(recyclerViewTopTen)

        val recyclerViewBaseCategory = binding.recyclerViewHomeCategory

        val categoryItems = listOf(
            HomeCategoryItem("진", "허브와 시트러스 향", R.drawable.category_jin),
            HomeCategoryItem("럼", "사탕수수의 달콤함", R.drawable.category_rum),
            HomeCategoryItem("보드카", "중립적이고 깨끗한 맛", R.drawable.category_vodka),
            HomeCategoryItem("위스키", "깊고 진한 풍미", R.drawable.category_whiskey),
            HomeCategoryItem("데킬라", "선명하고 강렬한 맛", R.drawable.category_tequila),
            HomeCategoryItem("리큐어", "과일, 허브 ,향신료", R.drawable.category_liqueur),
            HomeCategoryItem("와인", "부드러운 여운", R.drawable.category_wine),
            HomeCategoryItem("브랜디", "부드러운 목넘김", R.drawable.category_brandy),
            HomeCategoryItem("기타", "K-술, 무알콜 등", R.drawable.category_etc)
        )

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1 // 모든 아이템 동일한 크기 유지
            }
        }

        baseCategoryAdapter = BaseCategoryAdapter(requireContext(), categoryItems)

        // RecyclerView 설정
        recyclerViewBaseCategory.apply {
            layoutManager = gridLayoutManager
            adapter = baseCategoryAdapter
            setHasFixedSize(true) // 성능 최적화
        }
        recyclerViewBaseCategory.adapter = baseCategoryAdapter
    }

    fun initText() {
        var fullText = "지금 당신을 위한\n완벽한 레시피를\n추천해드릴게요!"
        var spannableString = SpannableString(fullText)
        var startIndex = fullText.indexOf("완벽한 레시피")
        var endIndex = startIndex + "완벽한 레시피".length

        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.basic_pink)),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.textViewHomePictureBig.text = spannableString

        fullText = "칵테일 초이스가 어렵다면\n베이스부터 골라보는 건 어떨까요?"
        spannableString = SpannableString(fullText)
        startIndex = fullText.indexOf("베이스")
        endIndex = startIndex + "베이스".length

        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.basic_sky)),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.textViewHomeCategory.text = spannableString

        fullText = "가장 많은 사랑을 받은\n인기 칵테일 TOP 10!"
        spannableString = SpannableString(fullText)
        startIndex = fullText.indexOf("TOP 10!")
        endIndex = startIndex + "TOP 10!".length

        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.basic_pink)),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.textViewHomeTopTen.text = spannableString
    }

    fun initEvent() {
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

data class HomeCategoryItem(
    val name: String,
    val explanation: String,
    val imageRes: Int
)

