package com.hontail.ui.cocktail.screen

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.hontail.databinding.FragmentCocktailDeleteDialogBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.cocktail.viewmodel.CocktailDetailFragmentViewModel

class CocktailDeleteDialogFragment(private val cocktailId: Int): DialogFragment() {

    private lateinit var binding: FragmentCocktailDeleteDialogBinding

    private lateinit var mainActivity: MainActivity

    private val viewModel: CocktailDetailFragmentViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val window = dialog?.window
        val params = window?.attributes

        // 화면 너비에서 20dp 양쪽 margin을 제외한 크기 계산
        val marginInPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics
        ).toInt()
        params?.width = resources.displayMetrics.widthPixels - (marginInPx * 2)
        params?.height = ViewGroup.LayoutParams.WRAP_CONTENT

        // 설정된 LayoutParams를 다이얼로그에 적용
        window?.attributes = params
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCocktailDeleteDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
    }

    private fun initEvent() {

        binding.apply {

            // 취소 이벤트
            buttonCocktailDetailDeleteCancel.setOnClickListener {
                dismiss()
            }

            // 확인 이벤트
            buttonCocktailDetailDeleteConfirm.setOnClickListener {

                viewModel.deleteCustomCocktail(cocktailId)
                parentFragmentManager.popBackStack("CocktailDetailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                dismiss()
            }
        }
    }
}