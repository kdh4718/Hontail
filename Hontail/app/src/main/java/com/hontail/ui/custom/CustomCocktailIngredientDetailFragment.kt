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
import com.hontail.util.CommonUtils

class CustomCocktailIngredientDetailFragment : BaseFragment<FragmentCustomCocktailIngredientDetailBinding>(
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

        initToolbar()
        initEvent()
    }

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

    private fun initEvent() {
        binding.apply {
            // 단위 선택 - 화살표 아이콘 클릭 시
            imageViewCustomCocktailIngredientDetailUnit.setOnClickListener {
                // 현재 선택된 단위를 전달
                val currentUnit = textViewCustomCocktailIngredientDetailUnit.text.toString()
                val bottomSheetFragment = CustomCocktailBottomSheetFragment.newInstance(currentUnit).apply {
                    unitSelectListener = object : CustomCocktailBottomSheetFragment.UnitSelectListener {
                        override fun onUnitSelected(unit: String) {
                            textViewCustomCocktailIngredientDetailUnit.text = unit
                        }
                    }
                }
                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }

            // 재료 추가 버튼
            buttonCustomCocktailIngredientDetail.setOnClickListener {
                parentFragmentManager.popBackStack("CustomCocktailIngredientDetailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }
}