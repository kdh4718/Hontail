package com.hontail.ui.cocktail.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hontail.data.model.response.Recipe
import com.hontail.ui.cocktail.screen.RecipeStepFragment

class CocktailRecipeViewPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val recipes: List<Recipe>  // 단일 레시피 대신 전체 리스트를 받음
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = recipes.size  // 전체 레시피 수만큼 페이지 생성

    override fun createFragment(position: Int): Fragment {
        return RecipeStepFragment.newInstance(recipes[position])
    }
}