package com.hontail.ui.custom

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCustomCocktailIngredientDetailBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class CustomCocktailIngredientDetailFragment: BaseFragment<FragmentCustomCocktailIngredientDetailBinding>(
    FragmentCustomCocktailIngredientDetailBinding::bind,
    R.layout.fragment_custom_cocktail_ingredient_detail
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(true)
        initToolbar()
        initEvent()
    }

    // 툴바 설정
    private fun initToolbar() {

        binding.apply {

            toolbarCustomCocktailIngredientDetail.apply {

                setNavigationIcon(R.drawable.go_back)
                setNavigationOnClickListener {
                    parentFragmentManager.popBackStack("CustomCocktailIngredientDetailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }
    }

    // 이벤트 설정
    private fun initEvent() {

        binding.apply {

            // 바텀 시트 띄우기.
            imageViewCustomCocktailIngredientDetailUnit.setOnClickListener {

                val bottomSheetFragment = CustomCocktailBottomSheetFragment()
                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }
        }
    }
}