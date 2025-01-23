package com.hontail.ui.ingredient

import android.content.Context
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentIngredientListBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class IngredientListFragment : BaseFragment<FragmentIngredientListBinding>(
    FragmentIngredientListBinding::bind,
    R.layout.fragment_ingredient_list
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
}