package com.hontail.ui.cocktail

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCocktailDetailBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class CocktailDetailFragment : BaseFragment<FragmentCocktailDetailBinding>(
    FragmentCocktailDetailBinding::bind,
    R.layout.fragment_cocktail_detail
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
            imageViewCocktailDetailComment.setOnClickListener {
                val bottomSheetFragment = CocktailCommentBottomSheetFragment()
                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }
        }
    }
}