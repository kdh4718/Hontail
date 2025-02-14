package com.hontail.ui.mypage.screen

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentMyPageModifyBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.mypage.viewmodel.MyPageViewModel
import com.hontail.util.CommonUtils

class MyPageModifyFragment : BaseFragment<FragmentMyPageModifyBinding>(
    FragmentMyPageModifyBinding::bind,
    R.layout.fragment_my_page_modify
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

        observeMyPage()
        initToolbar()
        initEvent()
    }

    // 툴바 설정
    private fun initToolbar() {

        binding.apply {

            toolbarMyPageModify.apply {

                setNavigationIcon(R.drawable.go_back)
                setNavigationOnClickListener {
                    parentFragmentManager.popBackStack("MyPageModifyFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }
    }

    // Observe ViewModel
    private fun observeMyPage() {

        binding.apply {

            viewModel.userInfo.observe(viewLifecycleOwner) { userInfo ->

                Glide.with(mainActivity)
                    .load(userInfo.user_image_url)
                    .into(imageViewMyPageModifyProfile)

                textViewMyPageModifyNickname.text = userInfo.user_nickname
                textViewMyPageModifyLoginAccount.text = userInfo.user_email
            }
        }
    }

    // 이벤트
    private fun initEvent() {

        binding.apply {

            // 사진 수정
            textViewMyPageModifyProfileEdit.setOnClickListener {

            }

            // 닉네임 변경
            buttonMyPageModifyNicknameChange.setOnClickListener {

                mainActivity.changeFragment(CommonUtils.MainFragmentName.MY_PAGE_NICKNAME_MODIFY_FRAGMENT)
            }
        }
    }
}