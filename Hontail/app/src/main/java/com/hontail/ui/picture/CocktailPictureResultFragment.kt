package com.hontail.ui.picture

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCocktailPictureResultBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.util.CommonUtils

class CocktailPictureResultFragment : BaseFragment<FragmentCocktailPictureResultBinding>(
    FragmentCocktailPictureResultBinding::bind,
    R.layout.fragment_cocktail_picture_result
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val bottomSheet = FilterBottomSheetFragment()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        initText()
        initEvent()
    }

    fun initAdapter() {
//        val recyclerView = binding.recyclerViewPictureResultIngredient // RecyclerView ID에 맞게 수정
//
//        val layoutManager = FlexboxLayoutManager(requireContext()).apply {
//            flexWrap = FlexWrap.WRAP
//            justifyContent = JustifyContent.FLEX_START
//        }
//        recyclerView.layoutManager = layoutManager
//
//        val dataList = listOf(
//            "Salt", "Mint", "Sugar", "Lime", "Ice",
//            "Rum", "Soda", "Basil", "Peach", "Cherry",
//            "Lemon", "Orange"
//        )
//
//        recyclerView.adapter = TextAdapter(dataList)
    }

    fun initText() {
        val fullText = "hyunn님, 오늘은 이 재료로\n딱 맞는 칵테일을 만들어 볼까요?"
        var spannableString = SpannableString(fullText)
        var startIndex = fullText.indexOf("hyunn")
        var endIndex = startIndex + "hyunn".length

        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.basic_sky)),
            startIndex,
            endIndex,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.textViewPictureResultSuggestion.text = spannableString
    }

    fun initEvent() {
        binding.apply {
            imageViewPictureResultFilter.setOnClickListener {
                val bottomSheetFragment = FilterBottomSheetFragment.newInstance(true)
                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }

            imageViewPictureResultAdd.setOnClickListener {

                CommonUtils.showDialog(
                    requireContext(),
                    "혹시 찍은 재료가 없나요?",
                    "없다면 재료를 등록해보세요!"
                ){
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.INGREDIENT_ADD_FRAGMENT)
                }
            }
        }
    }
}
