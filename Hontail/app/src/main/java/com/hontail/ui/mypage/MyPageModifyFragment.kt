package com.hontail.ui.mypage

import android.content.Context
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentMyPageModifyBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class MyPageModifyFragment : BaseFragment<FragmentMyPageModifyBinding>(
    FragmentMyPageModifyBinding::bind,
    R.layout.fragment_my_page_modify
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
}