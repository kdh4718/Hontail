package com.hontail.ui.cocktail.screen

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.hontail.base.BaseFragment
import com.hontail.R
import com.hontail.databinding.DrawerCocktailRecipeBinding
import com.hontail.databinding.FragmentCocktailRecipeBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.cocktail.adapter.CocktailRecipeViewPagerAdapter
import com.hontail.ui.cocktail.adapter.CocktailRecipeDrawerAdapter
import com.hontail.ui.cocktail.viewmodel.CocktailDetailFragmentViewModel
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan

class CocktailRecipeFragment : BaseFragment<DrawerCocktailRecipeBinding>(
    DrawerCocktailRecipeBinding::bind,
    R.layout.drawer_cocktail_recipe
) {
    private var _contentBinding: FragmentCocktailRecipeBinding? = null
    private val contentBinding get() = _contentBinding!!

    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CocktailDetailFragmentViewModel by viewModels()
    private lateinit var drawerAdapter: CocktailRecipeDrawerAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cocktailId = activityViewModel.cocktailId.value ?: 0
        viewModel.userId = activityViewModel.userId

        // 칵테일 상세 정보 가져오기
        viewModel.getCocktailDetailInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNav(true)
        _contentBinding = FragmentCocktailRecipeBinding.bind(binding.includeDrawerCocktailRecipeInclude.root)

        // 스와이프로 드로어를 열고 닫는 기능 비활성화
        binding.drawerLayoutDrawerCocktailRecipeDrawer.setDrawerLockMode(
            androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )

        binding.drawerLayoutDrawerCocktailRecipeDrawer.setBackgroundColor(
            ContextCompat.getColor(requireContext(), android.R.color.transparent)
        )

        initObserver()
        initEvent()
        initViewPager()
    }
    override fun onResume() {
        super.onResume()
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun initObserver() {
        viewModel.cocktailInfo.observe(viewLifecycleOwner) { cocktailDetail ->
            cocktailDetail?.let { detail ->

                // 칵테일 이름 설정 추가
                contentBinding.textViewCocktailRecipeTitle.text = "${detail.cocktailName} 레시피"

                // ViewPager 설정
                contentBinding.viewPagerCocktailRecipeViewPager.adapter =
                    CocktailRecipeViewPagerAdapter(mainActivity, detail.recipes)

                // 프로그레스 바 설정
                val totalSteps = detail.recipes.size
                contentBinding.indicatorCocktailRecipeIndicator.max = totalSteps
                updateProgress(1, totalSteps)

                // 드로어 리사이클러뷰 설정
                drawerAdapter = CocktailRecipeDrawerAdapter(detail.recipes)
                binding.navigationViewDrawerCocktailRecipeNavigation
                    .findViewById<RecyclerView>(R.id.recyclerViewDrawerRecipes).apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = drawerAdapter

                        // 드로어 아이템 클릭 시 해당 페이지로 이동
                        drawerAdapter.setOnItemClickListener { position ->
                            contentBinding.viewPagerCocktailRecipeViewPager.currentItem = position
                            binding.drawerLayoutDrawerCocktailRecipeDrawer.closeDrawers()
                        }
                    }
            }
        }
    }

    private fun initViewPager() {
        contentBinding.viewPagerCocktailRecipeViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val totalSteps = viewModel.cocktailInfo.value?.recipes?.size ?: 0
                    updateProgress(position + 1, totalSteps)
                    // 드로어의 선택된 항목 업데이트
                    drawerAdapter.updateSelectedPosition(position)
                }
            }
        )
    }

    private fun updateProgress(currentStep: Int, totalSteps: Int) {
        // 프로그레스 바 업데이트
        contentBinding.indicatorCocktailRecipeIndicator.progress = currentStep

        // SpannableString을 사용하여 텍스트 일부분의 색상 변경
        val fullText = "총 ${totalSteps}단계 중 ${currentStep}단계 제조중"
        val spannableString = SpannableString(fullText)

        // "중" 이후에 나오는 currentStep의 위치를 찾기
        val middleText = "중 "
        val middleIndex = fullText.indexOf(middleText)
        val startIndex = fullText.indexOf(currentStep.toString(), middleIndex)
        val endIndex = startIndex + currentStep.toString().length + 2 // "단계" 포함

        // 해당 부분에 색상 적용
        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.basic_sky)),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // 드로어의 진행 상태 텍스트 업데이트
        binding.textViewDrawerCocktailRecipeStep.text = spannableString
    }

    private fun initEvent() {
        binding.apply {
            includeDrawerCocktailRecipeInclude.imageViewCocktailRecipeGoBack.setOnClickListener {
                mainActivity.onBackPressed()
            }

            includeDrawerCocktailRecipeInclude.imageButtonCocktailRecipeSideBar.setOnClickListener {
                drawerLayoutDrawerCocktailRecipeDrawer.openDrawer(GravityCompat.END)
            }

            imageViewDrawerCocktailRecipeClose.setOnClickListener {
                drawerLayoutDrawerCocktailRecipeDrawer.closeDrawers()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNav(false)
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        _contentBinding = null
    }
}