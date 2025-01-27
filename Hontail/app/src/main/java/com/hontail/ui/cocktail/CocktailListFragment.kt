package com.hontail.ui.cocktail

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentBatenderBinding
import com.hontail.databinding.FragmentCocktailListBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class CocktailListFragment : BaseFragment<FragmentCocktailListBinding>(
    FragmentCocktailListBinding::bind,
    R.layout.fragment_cocktail_list
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity.hideBottomNav(true)
    }
}