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
import com.hontail.ui.picture.FilterBottomSheetFragment

class CocktailListFragment : BaseFragment<FragmentCocktailListBinding>(
    FragmentCocktailListBinding::bind,
    R.layout.fragment_cocktail_list
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
    }

    fun initEvent(){
        binding.apply {
            imageViewCocktailListCategory.setOnClickListener {
                val bottomSheetFragment = FilterBottomSheetFragment.newInstance(false)
                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }
        }
    }
}