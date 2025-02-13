package com.hontail.ui.custom.screen

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
import com.hontail.ui.custom.viewmodel.CustomCocktailIngredientDetailViewModel

class CustomCocktailIngredientDetailFragment : BaseFragment<FragmentCustomCocktailIngredientDetailBinding>(
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
            // 단위 선택 - 화살표 아이콘 클릭 시
            textViewCustomCocktailIngredientDetailUnit.setOnClickListener {

                val currentSelectedUnit = textViewCustomCocktailIngredientDetailUnit.text.toString()

                val bottomSheetFragment = CustomCocktailBottomSheetFragment.newInstance(currentSelectedUnit)

                bottomSheetFragment.onUnitSelectedListener = object : OnUnitSelectedListener {
                    override fun onUnitSelected(unitItem: UnitItem) {
                        textViewCustomCocktailIngredientDetailUnit.text = unitItem.unitName
                    }
                }

                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }

            // 재료 추가 버튼
            buttonCustomCocktailIngredientDetail.setOnClickListener {

                viewModel.ingredientDetailInfo.value?.let { ingredient ->

                    val ingredientQuantity = "${editTextCustomCocktailIngredientDetailContent.text} ${textViewCustomCocktailIngredientDetailUnit.text}"

                    val newItem = CustomCocktailItem.IngredientItem(
                        ingredient.ingredientId,
                        ingredient.ingredientNameKor,
                        ingredientQuantity,
                        ingredient.ingredientImage,
                        ingredient.ingredientAlcoholContent.toDouble(),
                        ingredient.ingredientCategoryKor,
                        ingredient.ingredientType
                    )
                    // 공유 ViewModel(MainActivityViewModel)에 재료 추가
                    activityViewModel.addCustomCocktailIngredient(newItem)
                }

                parentFragmentManager.popBackStack("CustomCocktailIngredientDetailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                parentFragmentManager.popBackStack("CustomCocktailSearchFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        }
    }
}