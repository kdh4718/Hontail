package com.hontail.ui.mypage.screen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentMyPageNicknameModifyBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.mypage.viewmodel.MyPageViewModel
import com.hontail.util.CommonUtils

private const val TAG = "MyPageNicknameModifyFra"
class MyPageNicknameModifyFragment: BaseFragment<FragmentMyPageNicknameModifyBinding>(
    FragmentMyPageNicknameModifyBinding::bind,
    R.layout.fragment_my_page_nickname_modify
) {

    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: MyPageViewModel by viewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(state = true)

        observeMyPageNicknameModify()
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

    // ViewModel Observe
    private fun observeMyPageNicknameModify() {

        binding.apply {

            viewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->
                editTextMyPageModifyNickname.setText(userInfo.user_nickname)
            }

            viewModel.isOkay.observe(viewLifecycleOwner) { isOkay ->

                // 제대로 변경 되었으면
                if(isOkay == true) {
                    parentFragmentManager.popBackStack("MyPageNicknameModifyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    parentFragmentManager.popBackStack("MyPageModifyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
                // 중복되는 닉네임이 있으면.
                else {
                    textViewMyPageNicknameModifyResult.visibility = View.VISIBLE
                    textViewMyPageNicknameModifyResult.text = "이미 존재하는 닉네임입니다."
                    textViewMyPageNicknameModifyResult.setTextAppearance(R.style.Font_Medium_16_Basic_Pink)
                }
            }
        }
    }

    // 이벤트 처리
    private fun initEvent() {
        binding.apply {

            // 닉네임 검사 버튼 클릭 이벤트
            buttonMyPageNicknameModifyCheck.setOnClickListener {

                val newNickname = editTextMyPageModifyNickname.text.toString()
                val currentName = viewModel.userInfo.value?.user_nickname

                if(newNickname == currentName) {
                    textViewMyPageNicknameModifyResult.visibility = View.VISIBLE
                    textViewMyPageNicknameModifyResult.text = "이미 존재하는 닉네임입니다."
                    textViewMyPageNicknameModifyResult.setTextAppearance(R.style.Font_Medium_16_Basic_Pink)
                    return@setOnClickListener
                }

                if(newNickname.isNullOrEmpty()) {
                    textViewMyPageNicknameModifyResult.visibility = View.VISIBLE
                    textViewMyPageNicknameModifyResult.text = "닉네임을 입력해주세요."
                    textViewMyPageNicknameModifyResult.setTextAppearance(R.style.Font_Medium_16_Basic_Sky)
                }
            }

            buttonMyPageNicknameModifyChange.setOnClickListener {

                val newNickname = editTextMyPageModifyNickname.text.toString()
                viewModel.modifyUserNickname(newNickname)
            }
        }
    }
}