package com.hontail.ui.bartender

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.R.*
import com.hontail.R.color.*
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentBatenderBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class BatenderFragment : BaseFragment<FragmentBatenderBinding>(
    FragmentBatenderBinding::bind,
    layout.fragment_batender
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var bartenderAdapter: BartenderAdapter

    private val messages = mutableListOf<ChatMessage>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(state = true)
        initToolbar()
        initAdapter()
        initEvent()
    }

    // 툴바 설정
    private fun initToolbar() {
        binding.apply {
            toolbarBartender.apply {
                setNavigationIcon(drawable.go_back)
                setNavigationOnClickListener {
                    parentFragmentManager.popBackStack("bartenderFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                }
            }
        }
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            val messages = listOf(
                ChatMessage("hyuun님 안녕하세요!\n바텐더 칵테일러 스위프트예요.1", false, "오전 08:13"),
                ChatMessage("hyuun님 안녕하세요!\n바텐더 칵테일러 스위프트예요.2", false, "오전 08:13"),
                ChatMessage("오늘은 좀 꿀꿀해.\n이런 날엔 어떤 칵테일을 먹으면 좋을까?", true, "오전 08:14"),
                ChatMessage("hyuun님 안녕하세요!\n바텐더 칵테일러 스위프트예요.1", false, "오전 08:15"),
                ChatMessage("hyuun님 안녕하세요!\n바텐더 칵테일러 스위프트예요.2", false, "오전 08:15"),
                ChatMessage("오늘은 좀 꿀꿀해.\n이런 날엔 어떤 칵테일을 먹으면 좋을까?", true, "오전 08:15"),
                ChatMessage("hyuun님 안녕하세요!\n바텐더 칵테일러 스위프트예요.1", false, "오전 08:17"),
                ChatMessage("hyuun님 안녕하세요!\n바텐더 칵테일러 스위프트예요.2", false, "오전 08:18"),
            )

            bartenderAdapter = BartenderAdapter(messages)

            recyclerViewBartender.adapter = bartenderAdapter
            recyclerViewBartender.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    // 이벤트들
    private fun initEvent() {
        binding.apply {

            // 키보드 올라오면 recyclerview 맨 마지막으로 이동 (근데 적용 안 됨)
            editTextBartenderMessage.setOnFocusChangeListener { _, hasFocus ->
                if(hasFocus) {
                    scrollToLastMessage()
                }
            }
        }
    }

    // 키보드 올라오면 맨 마지막 아이템으로 가기.
    private fun scrollToLastMessage() {
        binding.apply {
            recyclerViewBartender.postDelayed({
                if(messages.isNotEmpty()) {
                    recyclerViewBartender.smoothScrollToPosition(messages.size - 1)
                }
            }, 100)
        }
    }

}

data class ChatMessage(
    val message: String,
    val isUser: Boolean,
    val timestamp: String
)