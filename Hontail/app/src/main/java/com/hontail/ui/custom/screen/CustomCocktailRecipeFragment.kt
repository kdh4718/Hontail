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

            recipeItems = mutableListOf(
                CustomCocktailRecipeItem.CustomCocktailRecipeImage,
                CustomCocktailRecipeItem.CustomCocktailAlcoholLevel(25),  // 인덱스 1 – 나중에 업데이트할 예정
                CustomCocktailRecipeItem.CustomCocktailDescription("맛있는 칵테일입니다."),
                CustomCocktailRecipeItem.CustomCocktailRecipeStep(
                    mutableListOf(
                        CocktailRecipeStep(1, CommonUtils.CustomCocktailRecipeAnimationType.DEFAULT, "")
                    )
                ),
                CustomCocktailRecipeItem.CustomCocktailRecipeAddStep,
                CustomCocktailRecipeItem.CustomCocktailRecipeRegister
            )

            customCocktailRecipeAdapter = CustomCocktailRecipeAdapter(mainActivity, recipeItems)

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

                // 레시피 단계 삭제.
                override fun onClickDeleteStep() {

                }
            }
        }
    }

    // 새로운 레시피 단계 항목 추가.
    private fun addNewRecipeStep() {
        val recipeStepIndex = recipeItems.indexOfFirst { it is CustomCocktailRecipeItem.CustomCocktailRecipeStep }

        if (recipeStepIndex == -1) {
            // 만약 리스트에 레시피 스텝이 없으면, 새로 추가
            val newStepList = mutableListOf(CocktailRecipeStep(1, CommonUtils.CustomCocktailRecipeAnimationType.DEFAULT, ""))
            recipeItems.add(3, CustomCocktailRecipeItem.CustomCocktailRecipeStep(newStepList))
            customCocktailRecipeAdapter.notifyItemInserted(3)
        } else {
            // 기존의 레시피 스텝을 찾아서 업데이트
            val recipeStepItem = recipeItems[recipeStepIndex] as CustomCocktailRecipeItem.CustomCocktailRecipeStep

            if (recipeStepItem.recipeStepList.size >= 15) {
                Toast.makeText(mainActivity, "최대 15단계까지만 추가할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return
            }

            // 새로운 스텝 추가
            val newStepNumber = recipeStepItem.recipeStepList.size + 1
            recipeStepItem.recipeStepList.add(CocktailRecipeStep(newStepNumber, CommonUtils.CustomCocktailRecipeAnimationType.DEFAULT, ""))

            // 리스트에 존재하는 요소를 직접 변경했으므로 notifyItemChanged() 호출
            customCocktailRecipeAdapter.notifyItemChanged(recipeStepIndex)
        }
    }


}

sealed class CustomCocktailRecipeItem {
    object CustomCocktailRecipeImage: CustomCocktailRecipeItem()
    data class CustomCocktailAlcoholLevel(val alcoholLevel: Int): CustomCocktailRecipeItem()
    data class CustomCocktailDescription(val description: String): CustomCocktailRecipeItem()
    data class CustomCocktailRecipeStep(var recipeStepList: MutableList<CocktailRecipeStep>): CustomCocktailRecipeItem()
    object CustomCocktailRecipeAddStep: CustomCocktailRecipeItem()
    object CustomCocktailRecipeRegister: CustomCocktailRecipeItem()
}

data class CocktailRecipeStep(val stepNumber: Int, val selectedAnimation: CommonUtils.CustomCocktailRecipeAnimationType, var description: String)