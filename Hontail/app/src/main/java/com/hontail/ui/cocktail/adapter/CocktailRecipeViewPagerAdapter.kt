package com.hontail.ui.cocktail.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hontail.data.model.response.Recipe
import com.hontail.ui.cocktail.screen.RecipeStepFragment

class CocktailRecipeViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val recipe: Recipe
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 1  // 첫번째 레시피만 표시

    override fun createFragment(position: Int): Fragment {
        return RecipeStepFragment.newInstance(recipe)
    }
}