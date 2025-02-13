package com.hontail.ui.custom.screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.api.client.testing.util.TestableByteArrayOutputStream
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentCustomCocktailRecipeStepBottomSheetBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

private const val TAG = "CustomCocktailRecipeSte"
class CustomCocktailRecipeStepBottomSheetFragment: BaseBottomSheetFragment<FragmentCustomCocktailRecipeStepBottomSheetBinding>(
    FragmentCustomCocktailRecipeStepBottomSheetBinding::bind,
    R.layout.fragment_custom_cocktail_recipe_step_bottom_sheet
) {

    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initEvent()
    }

    private fun initView() {

        binding.apply {

            val currentStepNumber = (activityViewModel.recipeSteps.value?.size ?: 0) + 1
            textViewCustomCocktailRecipeStepBottomSheetSequence.text = currentStepNumber.toString()
        }
    }

    private fun initEvent() {

        binding.apply {

            buttonCustomCocktailRecipeStepBottomSheetAdd.setOnClickListener {

                Log.d(TAG, "initEvent: 레시피 추가 버튼 클릭됨")

                val action = when {
                    radioButtonCustomCocktailRecipeStepBottomSheetDefault.isChecked -> null
                    radioButtonCustomCocktailRecipeStepBottomSheetPour.isChecked -> "pour"
                    radioButtonCustomCocktailRecipeStepBottomSheetShake.isChecked -> "shake"
                    radioButtonCustomCocktailRecipeStepBottomSheetStir.isChecked -> "stir"
                    else -> null
                }

                val guide = editTextCustomCocktailRecipeStepBottomSheetDescription.text.toString()

                if(guide.isNotBlank()) {
                    Log.d(TAG, "initEvent: action=$action guide=$guide")

                    activityViewModel.addNewRecipeStep(action, guide)
                    Log.d(TAG, "initEvent: addNewRecipeStep 호출 완료")
                    dismiss()
                }
                else {
                    Toast.makeText(context, "설명을 입력해주세요!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}