package com.hontail.ui.custom.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCustomCocktailRecipeBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.custom.adapter.CustomCocktailRecipeAdapter
import com.hontail.util.CommonUtils

class CustomCocktailRecipeFragment: BaseFragment<FragmentCustomCocktailRecipeBinding>(
    FragmentCustomCocktailRecipeBinding::bind,
    R.layout.fragment_custom_cocktail_recipe
) {

    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var customCocktailRecipeAdapter: CustomCocktailRecipeAdapter

    private lateinit var recipeItems: MutableList<CustomCocktailRecipeItem>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeCustomCocktailRecipe()
        initToolbar()
        initAdapter()
        initEvent()
    }

    // 툴바 설정
    private fun initToolbar() {

        binding.apply {

            toolbarCustomCocktailRecipe.apply {

                setNavigationIcon(R.drawable.go_back)
                setNavigationOnClickListener {
                    parentFragmentManager.popBackStack("CustomCocktailRecipeFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }
    }

    // ViewModel Observe 등록
    private fun observeCustomCocktailRecipe() {

        binding.apply {

            activityViewModel.overallAlcoholContent.observe(viewLifecycleOwner) { overAllAlcohol ->
                val updatedAlcoholLevel = overAllAlcohol.toInt()
                recipeItems[1] = CustomCocktailRecipeItem.CustomCocktailAlcoholLevel(updatedAlcoholLevel)
                customCocktailRecipeAdapter.notifyItemChanged(1)
            }
        }
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {
        binding.apply {

//            val items = mutableListOf<CustomCocktailRecipeItem>().apply {
//                add(CustomCocktailRecipeItem.CustomCocktailRecipeImage)
//                add(CustomCocktailRecipeItem.CustomCocktailAlcoholLevel(25))
//                add(CustomCocktailRecipeItem.CustomCocktailDescription("맛있는 칵테일입니다."))
//                add(CustomCocktailRecipeItem.CustomCocktailRecipeStepHeader)
//                add(
//                    CustomCocktailRecipeItem.CustomCocktailRecipeStep(
//                        1,
//                        CommonUtils.CustomCocktailRecipeAnimationType.STIR,
//                        "열심히 저어주세요."
//                    )
//                )
//                add(CustomCocktailRecipeItem.CustomCocktailRecipeAddStep)
//                add(CustomCocktailRecipeItem.CustomCocktailRecipeRegister)
//            }

            recipeItems = mutableListOf(
                CustomCocktailRecipeItem.CustomCocktailRecipeImage,
                CustomCocktailRecipeItem.CustomCocktailAlcoholLevel(25),  // 인덱스 1 – 나중에 업데이트할 예정
                CustomCocktailRecipeItem.CustomCocktailDescription("맛있는 칵테일입니다."),
                CustomCocktailRecipeItem.CustomCocktailRecipeStepHeader,
                CustomCocktailRecipeItem.CustomCocktailRecipeStep(1, CommonUtils.CustomCocktailRecipeAnimationType.STIR, "열심히 저어주세요."),
                CustomCocktailRecipeItem.CustomCocktailRecipeAddStep,
                CustomCocktailRecipeItem.CustomCocktailRecipeRegister
            )

            customCocktailRecipeAdapter = CustomCocktailRecipeAdapter(recipeItems)

            recyclerViewCustomCocktailRecipe.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCustomCocktailRecipe.adapter = customCocktailRecipeAdapter
        }
    }

    // Event
    private fun initEvent() {

        binding.apply {

            customCocktailRecipeAdapter.customCocktailRecipeListener = object : CustomCocktailRecipeAdapter.ItemOnClickListener {

                // 등록
                override fun onClickRegister() {

                }

                // 레시피 단계 추가
                override fun onClickAddStep() {
                    addNewRecipeStep()
                }
            }
        }
    }

    // 새로운 레시피 단계 항목 추가.
    private fun addNewRecipeStep() {

        // CustomCocktailRecipeStep 항목만 세어 현재 단계 수 계산
        val currentStepCount = recipeItems.count { it is CustomCocktailRecipeItem.CustomCocktailRecipeStep }

        if (currentStepCount >= 15) {
            // 최대 단계가 15단계이면 추가하지 않고 사용자에게 알림
            Toast.makeText(mainActivity, "최대 15단계 까지만 추가할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        // 새 단계 번호는 기존 단계 수 + 1
        val newStepNumber = currentStepCount + 1

        // 새 레시피 단계 항목 생성 (기본 애니메이션은 STIR, 설명은 빈 문자열)
        val newStep = CustomCocktailRecipeItem.CustomCocktailRecipeStep(newStepNumber, CommonUtils.CustomCocktailRecipeAnimationType.STIR, "")

        // "레시피 추가" 항목 바로 위에 새 항목을 삽입
        val addStepIndex = recipeItems.indexOfFirst { it is CustomCocktailRecipeItem.CustomCocktailRecipeAddStep }

        if (addStepIndex != -1) {
            recipeItems.add(addStepIndex, newStep)
            customCocktailRecipeAdapter.notifyItemInserted(addStepIndex)
        }
    }

}

sealed class CustomCocktailRecipeItem {
    object CustomCocktailRecipeImage: CustomCocktailRecipeItem()
    data class CustomCocktailAlcoholLevel(val alcoholLevel: Int): CustomCocktailRecipeItem()
    data class CustomCocktailDescription(val description: String): CustomCocktailRecipeItem()
    object CustomCocktailRecipeStepHeader: CustomCocktailRecipeItem()
    data class CustomCocktailRecipeStep(val stepNumber: Int, val selectedAnimation: CommonUtils.CustomCocktailRecipeAnimationType, var description: String): CustomCocktailRecipeItem()
    object CustomCocktailRecipeAddStep: CustomCocktailRecipeItem()
    object CustomCocktailRecipeRegister: CustomCocktailRecipeItem()
}