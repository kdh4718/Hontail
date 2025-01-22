package com.hontail.ui.cocktail

import android.content.Context
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCocktailSearchBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class CocktailSearchFragment : BaseFragment<FragmentCocktailSearchBinding>(
    FragmentCocktailSearchBinding::bind,
    R.layout.fragment_cocktail_search
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
}