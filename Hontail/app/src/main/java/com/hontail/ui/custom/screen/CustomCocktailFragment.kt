package com.hontail.ui.custom.screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCustomCocktailBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.custom.adapter.CustomCocktailAdapter
import com.hontail.util.CommonUtils

private const val TAG = "CustomCocktailFragment"
class CustomCocktailFragment : BaseFragment<FragmentCustomCocktailBinding>(
    FragmentCustomCocktailBinding::bind,
    R.layout.fragment_custom_cocktail
) {
    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var customCocktailAdapter: CustomCocktailAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(true)

        observeCustomCocktail()
        initToolbar()
        initAdapter()
        initEvent()
    }

    // 툴바 설정
    private fun initToolbar() {
        binding.apply {
            toolbarCustomCocktail.apply {
                setNavigationIcon(R.drawable.go_back)
                setNavigationOnClickListener {
                    // 백스택에서 이전 fragment 확인
                    val count = parentFragmentManager.backStackEntryCount
                    if (count > 1) {
                        val previousEntry = parentFragmentManager.getBackStackEntryAt(count - 2)
                        // 이전 화면에 따라 bottom navigation 아이템 체크
                        when (previousEntry.name) {
                            "HomeFragment" -> mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_home
                            "CocktailListFragment" -> mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_search
                            "ZzimFragment" -> mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_heart
                            "MyPageFragment" -> mainActivity.binding.bottomNavigation.selectedItemId = R.id.navigation_mypage
                        }
                    }
                    parentFragmentManager.popBackStack("CustomCocktailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }
    }

    // ViewModel Observe 등록
    private fun observeCustomCocktail() {

        binding.apply {

            activityViewModel.customCocktailIngredients.observe(viewLifecycleOwner) { ingredientList ->

                // 비어 있지 않다면.
                if(ingredientList.isNotEmpty()) {
                    customCocktailAdapter.updateItems(ingredientList)
                }
                else {
                    customCocktailAdapter.updateItems(mutableListOf(CustomCocktailItem.EmptyItem))
                }
            }
            
            activityViewModel.overallAlcoholContent.observe(viewLifecycleOwner) { overAllAlcohol ->
                Log.d(TAG, "observeCustomCocktail:  ${String.format("%.1f", overAllAlcohol)}")
            }
        }
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            customCocktailAdapter = CustomCocktailAdapter(mutableListOf())

            recyclerViewCustomCocktail.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCustomCocktail.adapter = customCocktailAdapter
        }
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            // 재료 삭제
            customCocktailAdapter.customCocktailIngredientListener = object : CustomCocktailAdapter.ItemOnClickListener {
                override fun onClickDelete(position: Int) {
                    activityViewModel.deleteCustomCocktailIngredientAt(position)
                }
            }

            // 재료 추가
            fabCustomCocktailIngredientAdd.setOnClickListener {
                mainActivity.changeFragment(CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_SEARCH_FRAGMENT)
            }

            // 다음으로 넘어가기
            buttonCustomCocktailNext.setOnClickListener {
                activityViewModel.setRecipeMode(CommonUtils.CustomCocktailRecipeMode.REGISTER)
                mainActivity.changeFragment(CommonUtils.MainFragmentName.CUSTOM_COCKTAIL_RECIPE_FRAGMENT)
            }
        }
    }

}

sealed class CustomCocktailItem {

    data class IngredientItem(val ingredientId: Int, val ingredientName: String, val ingredientQuantity: String, val ingredientImage: String, val alcoholContent: Double, val ingredientCategoryKor: String, val ingredientType: String?): CustomCocktailItem()
    object EmptyItem: CustomCocktailItem()
}