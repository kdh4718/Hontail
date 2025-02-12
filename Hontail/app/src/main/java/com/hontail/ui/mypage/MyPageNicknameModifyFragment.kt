package com.hontail.ui.mypage

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentMyPageNicknameModifyBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.util.CommonUtils

private const val TAG = "MyPageNicknameModifyFra"

class MyPageNicknameModifyFragment: BaseFragment<FragmentMyPageNicknameModifyBinding>(
    FragmentMyPageNicknameModifyBinding::bind,
    R.layout.fragment_my_page_nickname_modify
) {

    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var isOkay = 3 // 기본값 (닉네임을 입력해주세요)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(state = true)
        initToolbar()
        initEvent()
    }

    // 툴바 설정
    private fun initToolbar() {
        binding.apply {
            toolbarMyPageNicknameModify.apply {
                setNavigationIcon(R.drawable.go_back)
                setNavigationOnClickListener {
                    parentFragmentManager.popBackStack(
                        "MyPageNicknameModifyFragment",
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                }
            }
        }
    }

    // 이벤트 처리
    private fun initEvent() {
        binding.apply {

            val isOkay = 1

            // 닉네임 검사 버튼 클릭 이벤트
            buttonMyPageNicknameModifyCheck.setOnClickListener {

                textViewMyPageNicknameModifyResult.visibility = View.VISIBLE

                when (isOkay) {
                    1 -> {
                        textViewMyPageNicknameModifyResult.text = "사용 가능한 닉네임입니다."
                        textViewMyPageNicknameModifyResult.setTextAppearance(R.style.Font_Medium_16_Basic_Sky)
                    }

                    2 -> {
                        textViewMyPageNicknameModifyResult.text = "이미 존재하는 닉네임입니다."
                        textViewMyPageNicknameModifyResult.setTextAppearance(R.style.Font_Medium_16_Basic_Pink)
                    }

                    3 -> {
                        textViewMyPageNicknameModifyResult.text = "닉네임을 입력해주세요."
                        textViewMyPageNicknameModifyResult.setTextAppearance(R.style.Font_Medium_16_Basic_Pink)
                    }
                }
            }

            // 변경 버튼 클릭 이벤트 (isOkay == 1일 때만 이동)
            buttonMyPageNicknameModifyChange.setOnClickListener {
                if (isOkay == 1) {
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.MY_PAGE_MODIFY_FRAGMENT)
                }
            }
        }
    }
}
