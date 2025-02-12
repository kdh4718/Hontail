package com.hontail.ui.bartender.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.hontail.databinding.BartenderDialogBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.util.CommonUtils

private const val TAG = "BartenderDialogFragment"
class BartenderDialogFragment(private var mode: CommonUtils.BartenderRecordMode): DialogFragment() {

    private lateinit var binding: BartenderDialogBinding

    private lateinit var mainActivity: MainActivity

    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var speechIntent: Intent

    var sendVoiceText = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BartenderDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.RECORD_AUDIO), 1001)
        }
        else {
            initSTT()
        }

        updateUI(mode)
        initEvent()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 1001) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initSTT()
            }
            else {
                Log.d(TAG, "onRequestPermissionsResult: 마이크 권한 거부됨")
            }
        }
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

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
        speechRecognizer = null
    }

    // 이벤트
    private fun initEvent() {
        binding.apply {

            // 녹음 시작
            imageViewDialogBartenderStart.setOnClickListener {
                changeMode(CommonUtils.BartenderRecordMode.RECORDING)

                // 녹음 기능 추가하기...
                speechRecognizer?.startListening(speechIntent)
            }

            // 녹음 중단
            imageViewDialogBartenderStop.setOnClickListener {
                changeMode(CommonUtils.BartenderRecordMode.COMPLETED)

                // 녹음 중지 기능 추가하기...
            }

            // 녹음 다시
            imageViewDialogBartenderAgain.setOnClickListener {
                changeMode(CommonUtils.BartenderRecordMode.RECORDING)

                // 녹음 기능 추가하기...
                speechRecognizer?.startListening(speechIntent)
            }

            // 녹음 전송
            imageViewDialogBartenderSend.setOnClickListener {
                // 녹음 파일 전송하기...

                activityViewModel.sendMessageToBartender(sendVoiceText)
                dismiss()
            }
        }
    }

    // 모드 변경
    private fun updateUI(mode: CommonUtils.BartenderRecordMode) {
        binding.apply {
            when(mode) {

                // 음성 녹음 준비 모드
                CommonUtils.BartenderRecordMode.READY -> {
//                    textViewDialogBartenderTitle.text = "말씀해주세요."
                    imageViewDialogBartenderStart.visibility = View.VISIBLE
                    imageViewDialogBartenderStop.visibility = View.GONE
                    imageViewDialogBartenderAgain.visibility = View.GONE
                    imageViewDialogBartenderSend.visibility = View.GONE
                }

                // 음성 녹음 중 모드
                CommonUtils.BartenderRecordMode.RECORDING -> {
//                    textViewDialogBartenderTitle.text = "듣고 있습니다."
                    imageViewDialogBartenderStart.visibility = View.GONE
                    imageViewDialogBartenderStop.visibility = View.VISIBLE
                    imageViewDialogBartenderAgain.visibility = View.GONE
                    imageViewDialogBartenderSend.visibility = View.VISIBLE
                }

                // 음성 녹음 완료 모드
                CommonUtils.BartenderRecordMode.COMPLETED -> {
//                    textViewDialogBartenderTitle.text = "다 들었습니다."
                    imageViewDialogBartenderStart.visibility = View.GONE
                    imageViewDialogBartenderStop.visibility = View.GONE
                    imageViewDialogBartenderAgain.visibility = View.VISIBLE
                    imageViewDialogBartenderSend.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun changeMode(newMode: CommonUtils.BartenderRecordMode) {
        mode = newMode
        updateUI(mode)
    }

    private fun initSTT() {
        if (!SpeechRecognizer.isRecognitionAvailable(requireContext())) {
            Log.d(TAG, "initSTT: 이 기기에서는 STT 기능 사용 불가")
            return
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext()).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    binding.textViewDialogBartenderTitle.text = "듣고 있어요..."
                }

                override fun onBeginningOfSpeech() {
                    binding.textViewDialogBartenderTitle.text = "녹음 중..."
                }

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    binding.textViewDialogBartenderTitle.text = "처리 중..."
                }

                override fun onError(error: Int) {
                    binding.textViewDialogBartenderTitle.text = "인식 실패! 다시 시도해 주세요."
                    Log.e(TAG, "STT 오류 발생: $error")
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val recognizedText = matches[0]

                        Log.d(TAG, "onResults: STT 결과 : $recognizedText")
                        binding.textViewDialogBartenderTitle.text = "인식 성공! 음성 녹음을 전송해주세요."
                        sendVoiceText = recognizedText
                    }
                    changeMode(CommonUtils.BartenderRecordMode.COMPLETED)
                }

                override fun onPartialResults(partialResults: Bundle?) {}

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
    }
}