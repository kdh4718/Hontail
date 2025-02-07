package com.hontail.ui.picture

import PictureBottomAdapter
import PictureTopAdapter
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ConcatAdapter
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentCocktailPictureResultBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class CocktailPictureResultFragment : BaseFragment<FragmentCocktailPictureResultBinding>(
    FragmentCocktailPictureResultBinding::bind,
    R.layout.fragment_cocktail_picture_result
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val bottomSheet = FilterBottomSheetFragment()
    private val topAdapter = PictureTopAdapter()
    private val bottomAdapter = PictureBottomAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
//        initText()
//        initEvent()
    }

    private fun initAdapter() {
        val topItem = PictureResultItem.TopItem(
            suggestion = "hyunn님, 오늘은 이 재료로\n딱 맞는 칵테일을 만들어 볼까요?"
        )
        topAdapter.setItem(topItem)

        val bottomItem = PictureResultItem.BottomItem(
            cocktailCount = "칵테일 24개"
        )
        bottomAdapter.setItem(bottomItem)
    }

//    fun initEvent() {
//        binding.apply {
//            imageViewPictureResultFilter.setOnClickListener {
////                val bottomSheetFragment = FilterBottomSheetFragment.newInstance(true)
////                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
//            }
//
//            imageViewPictureResultAdd.setOnClickListener {
//
//                CommonUtils.showDialog(
//                    requireContext(),
//                    "혹시 찍은 재료가 없나요?",
//                    "없다면 재료를 등록해보세요!"
//                ){
//                    mainActivity.changeFragment(CommonUtils.MainFragmentName.INGREDIENT_ADD_FRAGMENT)
//                }
//            }
//        }
//    }
//
//
//    private fun initText() {
//        // 필요한 경우 텍스트 초기화
//    }
//
//    private fun initEvent() {
//        // 이벤트 처리 로직
//    }
}

sealed class PictureResultItem {
    data class TopItem(
        val suggestion: String
    ) : PictureResultItem()

    data class BottomItem(
        val cocktailCount: String
    ) : PictureResultItem()
}
