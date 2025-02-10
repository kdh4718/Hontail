package com.hontail.ui.ingredient

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentIngredientAddBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class IngredientAddFragment : BaseFragment<FragmentIngredientAddBinding>(
    FragmentIngredientAddBinding::bind,
    R.layout.fragment_ingredient_add
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBackButton()
    }

    private fun setupBackButton() {
        binding.imageViewIngredientArrow.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}