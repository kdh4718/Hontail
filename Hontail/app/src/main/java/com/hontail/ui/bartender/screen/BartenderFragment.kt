package com.hontail.ui.bartender.screen

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R.*
import com.hontail.base.BaseFragment
import com.hontail.data.model.response.Cocktail
import com.hontail.databinding.FragmentBatenderBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.bartender.adapter.BartenderAdapter
import com.hontail.util.CommonUtils
import java.util.Locale

class BartenderFragment : BaseFragment<FragmentBatenderBinding>(
    FragmentBatenderBinding::bind,
    layout.fragment_batender
), TextToSpeech.OnInitListener {
    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private lateinit var bartenderAdapter: BartenderAdapter

    private var textToSpeech: TextToSpeech? = null  // ✅ TTS 객체 추가

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ TTS 초기화 (영어 기본)
        textToSpeech = TextToSpeech(mainActivity, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech?.apply {
            stop()
            shutdown()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity.hideBottomNav(state = true)
        activityViewModel.receiveBartenderGreeting()

        observeBartender()
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

    // ViewModel Observe 등록
    private fun observeBartender() {
        binding.apply {
            activityViewModel.messages.observe(viewLifecycleOwner) { messages ->
                bartenderAdapter.updateMessages(messages)
                scrollToLastMessage()

                // ✅ 새로운 메시지가 추가되었을 때, 마지막 메시지를 읽음
                messages.lastOrNull()?.let { lastMessage ->
                    if (!lastMessage.isUser) {  // ✅ 사용자 메시지가 아닐 때만 읽기
                        speakMessage(lastMessage.message)
                    }
                }
            }
        }
    }

    // 리사이클러뷰 어댑터 연결
    private fun initAdapter() {

        binding.apply {

            bartenderAdapter = BartenderAdapter(mainActivity, emptyList())

            recyclerViewBartender.adapter = bartenderAdapter
            recyclerViewBartender.layoutManager = LinearLayoutManager(mainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    // 이벤트들
    private fun initEvent() {
        binding.apply {

            // 음성 녹음 이벤트
            imageViewBartenderVoice.setOnClickListener {
                val dialog = BartenderDialogFragment(CommonUtils.BartenderRecordMode.READY)
                dialog.show(parentFragmentManager, "BartenderDialog")
            }

            // 보내기 이벤트
            imageViewBartenderSend.setOnClickListener {

                val messageText = editTextBartenderMessage.text.toString()

                if(messageText.isNotEmpty()) {
                    activityViewModel.sendMessageToBartender(messageText)
                    editTextBartenderMessage.text.clear()
                }
            }

            bartenderAdapter.bartenderListener = object : BartenderAdapter.ItemOnClickListener {

                // 칵테일 상세 화면으로 가기.
                override fun onClickCocktailImage(cocktailId: Int) {
                    activityViewModel.setCocktailId(cocktailId)
                    mainActivity.changeFragment(CommonUtils.MainFragmentName.COCKTAIL_DETAIL_FRAGMENT)
                }
            }
        }
    }

    // 키보드 올라오면 맨 마지막 아이템으로 가기.
    private fun scrollToLastMessage() {
        binding.apply {
            recyclerViewBartender.postDelayed({
                if(bartenderAdapter.itemCount > 0) {
                    recyclerViewBartender.smoothScrollToPosition(bartenderAdapter.itemCount - 1)
                }
            }, 100)
        }
    }

    // ✅ TTS를 활용한 메시지 음성 출력
    private fun speakMessage(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                tts.language = Locale.KOREAN  // ✅ 한국어 설정
                tts.setSpeechRate(1.0f)  // ✅ 속도 조절
                tts.setPitch(1.0f)       // ✅ 피치 조절

                // ✅ 사용할 음성의 이름 지정
                val voiceName = "ko-KR-default"  // 원하는 목소리 설정 (예: SMTl08)

                // ✅ 해당 목소리가 있는지 확인 후 설정
                val selectedVoice = tts.voices.find { it.name == voiceName }
                if (selectedVoice != null) {
                    tts.voice = selectedVoice
                    Log.d("TTS", "Selected voice: ${selectedVoice.name}")
                } else {
                    Log.e("TTS", "목소리를 찾을 수 없습니다: $voiceName")
                }
            }
        } else {
            Log.e("TTS", "초기화 실패")
        }
    }




}

data class ChatMessage(
    val message: String,
    val isUser: Boolean,
    val timestamp: String,
    val cocktail: Cocktail? = null
)