package com.hontail.ui.picture

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
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
                val bottomSheetFragment = FilterBottomSheetFragment.newInstance(true)
                bottomSheetFragment.show(parentFragmentManager, bottomSheetFragment.tag)
            }

            imageViewPictureResultAdd.setOnClickListener {
                // 다이얼로그를 생성
                val dialogView = layoutInflater.inflate(R.layout.custom_dialog_need_ingredient, null)

                // 다이얼로그 빌더 설정
                val dialog = AlertDialog.Builder(requireContext())
                    .setView(dialogView) // 다이얼로그에 XML 레이아웃 적용
                    .create()

                // 다이얼로그 표시
                dialog.show()

                // 확인 버튼 클릭 시 동작
                val confirmButton = dialogView.findViewById<MaterialButton>(R.id.buttonCustomDialogConfirm)
                confirmButton.setOnClickListener {
                    // 확인 버튼 클릭 시 처리할 내용
                    dialog.dismiss() // 다이얼로그 종료
                }

                // 취소 버튼 클릭 시 동작
                val cancelButton = dialogView.findViewById<MaterialButton>(R.id.buttonCustomDialogCancel)
                cancelButton.setOnClickListener {
                    // 취소 버튼 클릭 시 처리할 내용
                    dialog.dismiss() // 다이얼로그 종료
                }
            }

        }
    }

}
