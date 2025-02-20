package com.hontail.ui.cocktail.screen

import android.os.Bundle
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.hontail.R
import com.hontail.data.model.response.Recipe
import com.hontail.databinding.ListItemViewPagerCocktailRecipeBinding
import com.hontail.base.BaseFragment

class RecipeStepFragment : BaseFragment<ListItemViewPagerCocktailRecipeBinding>(
    ListItemViewPagerCocktailRecipeBinding::bind,
    R.layout.list_item_view_pager_cocktail_recipe
) {

    companion object {
        private const val ARG_RECIPE_SEQUENCE = "recipe_sequence"
        private const val ARG_RECIPE_GUIDE = "recipe_guide"
        private const val ARG_RECIPE_ACTION = "recipe_action"

        fun newInstance(recipe: Recipe): RecipeStepFragment {
            return RecipeStepFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_RECIPE_SEQUENCE, recipe.sequence)
                    putString(ARG_RECIPE_GUIDE, recipe.recipeGuide)
                    putString(ARG_RECIPE_ACTION, recipe.recipeAction)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sequence = arguments?.getInt(ARG_RECIPE_SEQUENCE) ?: 1
        val guide = arguments?.getString(ARG_RECIPE_GUIDE) ?: ""
        val action = arguments?.getString(ARG_RECIPE_ACTION)

        binding.apply {
            textViewCocktailRecipeStep.text = "Step ${sequence}"
            textViewCocktailRecipeStepExplanation.text = guide

            // LottieAnimationView를 찾아서 애니메이션 설정
            val lottieView = imageViewCocktailRecipeImage as? LottieAnimationView
            lottieView?.let {
                // 레시피 액션에 따라 다른 애니메이션 설정
                val animationFile = when (action) {
                    "pour" -> "cocktail_pour.json"
                    "shake" -> "cocktail_shake.json"
                    "stir" -> "cocktail_stir.json"
                    else -> "cocktail_etc.json"
                }

                it.setAnimation(animationFile)
                it.playAnimation()
            }
        }
    }
}