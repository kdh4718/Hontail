package com.hontail.ui.cocktail.screen

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.hontail.base.BaseFragment
import com.hontail.R
import com.hontail.databinding.DrawerCocktailRecipeBinding
import com.hontail.databinding.FragmentCocktailRecipeBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.cocktail.adapter.CocktailRecipeViewPagerAdapter
import com.hontail.ui.cocktail.viewmodel.CocktailDetailFragmentViewModel

class CocktailRecipeFragment : BaseFragment<DrawerCocktailRecipeBinding>(
    DrawerCocktailRecipeBinding::bind,
    R.layout.drawer_cocktail_recipe
) {
    private var _contentBinding: FragmentCocktailRecipeBinding? = null
    private val contentBinding get() = _contentBinding!!

    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CocktailDetailFragmentViewModel by viewModels()

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

        binding.drawerLayoutDrawerCocktailRecipeDrawer.setBackgroundColor(
            ContextCompat.getColor(requireContext(), android.R.color.transparent)
        )

        initObserver()
        initEvent()
    }

    override fun onResume() {
        super.onResume()
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun initObserver() {
        viewModel.cocktailInfo.observe(viewLifecycleOwner) { cocktailDetail ->
            cocktailDetail?.let { detail ->
                // 첫번째 레시피만 가져와서 ViewPager에 설정
                detail.recipes.firstOrNull()?.let { firstRecipe ->
                    contentBinding.viewPagerCocktailRecipeViewPager.adapter =
                        CocktailRecipeViewPagerAdapter(mainActivity, firstRecipe)
                }

                // 프로그레스 바 설정 (첫 단계이므로 전체 단계 중 1단계 표시)
                val totalSteps = detail.recipes.size
                contentBinding.indicatorCocktailRecipeIndicator.max = totalSteps
                contentBinding.indicatorCocktailRecipeIndicator.progress = 1
            }
        }
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