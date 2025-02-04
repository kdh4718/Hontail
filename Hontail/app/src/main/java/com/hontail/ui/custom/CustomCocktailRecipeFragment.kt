package com.hontail.ui.custom

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCustomCocktailRecipeBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.util.CommonUtils
import okhttp3.internal.addHeaderLenient

class CustomCocktailRecipeFragment: BaseFragment<FragmentCustomCocktailRecipeBinding>(
    FragmentCustomCocktailRecipeBinding::bind,
    R.layout.fragment_custom_cocktail_recipe
) {

    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var customCocktailRecipeAdapter: CustomCocktailRecipeAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        initAdapter()
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

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            val items = mutableListOf<CustomCocktailRecipeItem>().apply {
                add(CustomCocktailRecipeItem.CustomCocktailRecipeImage)
                add(CustomCocktailRecipeItem.CustomCocktailAlcoholLevel(25))
                add(CustomCocktailRecipeItem.CustomCocktailDescription("맛있는 칵테일입니다."))
                add(CustomCocktailRecipeItem.CustomCocktailRecipeStepHeader)
                add(CustomCocktailRecipeItem.CustomCocktailRecipeStep(1, CommonUtils.CustomCocktailRecipeAnimationType.STIR, "열심히 저어주세요."))
                add(CustomCocktailRecipeItem.CustomCocktailRecipeAddStep)
                add(CustomCocktailRecipeItem.CustomCocktailRecipeRegister)
            }

            customCocktailRecipeAdapter = CustomCocktailRecipeAdapter(items)

            recyclerViewCustomCocktailRecipe.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
            recyclerViewCustomCocktailRecipe.adapter = customCocktailRecipeAdapter
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