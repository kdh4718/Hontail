package com.hontail.ui.picture

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
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

    fun initEvent(){
        binding.apply {
            imageViewPictureResultFilter.setOnClickListener {
                val bottomSheetFragment = FilterBottomSheetFragment()
                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }
        }
    }

}
