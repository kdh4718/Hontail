package com.hontail.ui.cocktail.screen

import android.os.Bundle
import android.view.View
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

        fun newInstance(recipe: Recipe): RecipeStepFragment {
            return RecipeStepFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_RECIPE_SEQUENCE, recipe.sequence)
                    putString(ARG_RECIPE_GUIDE, recipe.recipeGuide)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sequence = arguments?.getInt(ARG_RECIPE_SEQUENCE) ?: 1
        val guide = arguments?.getString(ARG_RECIPE_GUIDE) ?: ""

        binding.textViewCocktailRecipeStep.text = "Step ${sequence}"
        binding.textViewCocktailRecipeStepExplanation.text = guide
    }
}