package com.hontail.ui.custom

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
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
    private val viewModel: CustomCocktailIngredientDetailViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.ingredientId = activityViewModel.ingredientId.value!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(true)
        observeIngredientDetail()
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

    // ViewModelObserver 등록.
    private fun observeIngredientDetail() {

        binding.apply {

            viewModel.ingredientDetailInfo.observe(viewLifecycleOwner) { ingredient ->

                textViewCustomCocktailIngredientDetailName.text = ingredient.ingredientNameKor

                Glide.with(mainActivity)
                    .load(ingredient.ingredientImage)
                    .placeholder(R.drawable.logo_image)
                    .into(imageViewCustomCocktailIngredientDetail)
            }
        }
    }

    // 이벤트 설정
    private fun initEvent() {

        binding.apply {

            // 바텀 시트 띄우기.
            imageViewCustomCocktailIngredientDetailUnit.setOnClickListener {

                val currentSelectedUnit = textViewCustomCocktailIngredientDetailUnit.text.toString()

                val bottomSheetFragment = CustomCocktailBottomSheetFragment.newInstance(currentSelectedUnit)

                bottomSheetFragment.onUnitSelectedListener = object : OnUnitSelectedListener {
                    override fun onUnitSelected(unitItem: UnitItem) {
                        textViewCustomCocktailIngredientDetailUnit.text = unitItem.unitName
                    }
                }

                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }

            // 재료 추가하기
            buttonCustomCocktailIngredientDetail.setOnClickListener {
                parentFragmentManager.apply {
                    popBackStack("CustomCocktailIngredientDetailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    popBackStack("CustomCocktailSearchFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }
    }
}