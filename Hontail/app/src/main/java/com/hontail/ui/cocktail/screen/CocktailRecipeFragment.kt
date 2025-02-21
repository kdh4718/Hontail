package com.hontail.ui.cocktail.screen

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.hontail.base.BaseFragment
import com.hontail.R
import com.hontail.databinding.DrawerCocktailRecipeBinding
import com.hontail.databinding.FragmentCocktailRecipeBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel
import com.hontail.ui.cocktail.adapter.CocktailRecipeViewPagerAdapter
import com.hontail.ui.cocktail.adapter.CocktailRecipeDrawerAdapter
import com.hontail.ui.cocktail.viewmodel.CocktailDetailFragmentViewModel
import java.util.Locale
import android.util.Log

private const val TAG = "CocktailRecipeFragment_SSAFY"
class CocktailRecipeFragment : BaseFragment<DrawerCocktailRecipeBinding>(
    DrawerCocktailRecipeBinding::bind,
    R.layout.drawer_cocktail_recipe
) {
    private var _contentBinding: FragmentCocktailRecipeBinding? = null
    private val contentBinding get() = _contentBinding!!

    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val viewModel: CocktailDetailFragmentViewModel by viewModels()
    private lateinit var drawerAdapter: CocktailRecipeDrawerAdapter

    companion object {
        private const val PERMISSION_CODE = 100
    }

    // STT 관련 변수
    private lateinit var speechRecognizer: SpeechRecognizer
    private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cocktailId = activityViewModel.cocktailId.value ?: 0
        viewModel.userId = activityViewModel.userId

        // 칵테일 상세 정보 가져오기
        viewModel.getCocktailDetailInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNav(true)
        _contentBinding = FragmentCocktailRecipeBinding.bind(binding.includeDrawerCocktailRecipeInclude.root)

        // 스와이프로 드로어를 열고 닫는 기능 비활성화
        binding.drawerLayoutDrawerCocktailRecipeDrawer.setDrawerLockMode(
            androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )

        binding.drawerLayoutDrawerCocktailRecipeDrawer.setBackgroundColor(
            ContextCompat.getColor(requireContext(), android.R.color.transparent)
        )

        checkPermission()
        initObserver()
        initEvent()
        initViewPager()
    }

    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_CODE)
        } else {
            initSpeechRecognizer()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            initSpeechRecognizer()
        }
    }

    private fun initSpeechRecognizer() {
        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
            speechRecognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    // 음성 인식이 끝나면 딜레이 후 다시 시작
                    Handler(Looper.getMainLooper()).postDelayed({
                        startListening()
                    }, 500)
                }

                override fun onError(error: Int) {
                    Log.d(TAG, "onError: $error")

                    Handler(Looper.getMainLooper()).postDelayed({
                        startListening()
                    }, 1000)
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    matches?.get(0)?.let { result ->
                        Log.d("STT_TEST", "인식된 음성: $result")
                        when {
                            result.contains("다음") -> {
                                val currentItem = contentBinding.viewPagerCocktailRecipeViewPager.currentItem
                                val totalItems = viewModel.cocktailInfo.value?.recipes?.size ?: 0
                                if (currentItem < totalItems - 1) {
                                    contentBinding.viewPagerCocktailRecipeViewPager.currentItem = currentItem + 1
                                }
                            }
                            result.contains("이전") -> {
                                val currentItem = contentBinding.viewPagerCocktailRecipeViewPager.currentItem
                                if (currentItem > 0) {
                                    contentBinding.viewPagerCocktailRecipeViewPager.currentItem = currentItem - 1
                                }
                            }
                        }
                    }
                    // 결과 처리 후 딜레이를 주고 재시작
                    Handler(Looper.getMainLooper()).postDelayed({
                        startListening()
                    }, 1000)
                }

                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            startListening()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("STT_TEST", "Speech Recognizer 초기화 실패: ${e.message}")
        }
    }

    private fun startListening() {
        try {
            speechRecognizer.startListening(recognizerIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("STT_TEST", "startListening 실패: ${e.message}")
        }
    }

    private fun initObserver() {
        viewModel.cocktailInfo.observe(viewLifecycleOwner) { cocktailDetail ->
            cocktailDetail?.let { detail ->
                contentBinding.textViewCocktailRecipeTitle.text = "${detail.cocktailName} 레시피"

                contentBinding.viewPagerCocktailRecipeViewPager.adapter =
                    CocktailRecipeViewPagerAdapter(mainActivity, detail.recipes)

                val totalSteps = detail.recipes.size
                contentBinding.indicatorCocktailRecipeIndicator.max = totalSteps
                updateProgress(1, totalSteps)
                // 초기에는 완료 버튼 숨기기
                contentBinding.textViewCocktailRecipeEnd.visibility = View.GONE

                drawerAdapter = CocktailRecipeDrawerAdapter(detail.recipes)
                binding.navigationViewDrawerCocktailRecipeNavigation
                    .findViewById<RecyclerView>(R.id.recyclerViewDrawerRecipes).apply {
                        layoutManager = LinearLayoutManager(context)
                        adapter = drawerAdapter

                        drawerAdapter.setOnItemClickListener { position ->
                            contentBinding.viewPagerCocktailRecipeViewPager.currentItem = position
                            binding.drawerLayoutDrawerCocktailRecipeDrawer.closeDrawers()
                        }
                    }
            }
        }
    }

    private fun initViewPager() {
        contentBinding.viewPagerCocktailRecipeViewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    val totalSteps = viewModel.cocktailInfo.value?.recipes?.size ?: 0
                    updateProgress(position + 1, totalSteps)
                    drawerAdapter.updateSelectedPosition(position)

                    // 마지막 페이지에서만 완료 버튼 표시
                    contentBinding.textViewCocktailRecipeEnd.visibility =
                        if (position == totalSteps - 1) View.VISIBLE else View.GONE
                }
            }
        )
    }

    private fun updateProgress(currentStep: Int, totalSteps: Int) {
        contentBinding.indicatorCocktailRecipeIndicator.progress = currentStep
        binding.textViewDrawerCocktailRecipeStep.text =
            "총 ${totalSteps}단계 중 ${currentStep}단계 제조중"
    }

    private fun initEvent() {
        binding.apply {
            includeDrawerCocktailRecipeInclude.imageViewCocktailRecipeGoBack.setOnClickListener {
                mainActivity.onBackPressed()
            }

            includeDrawerCocktailRecipeInclude.imageButtonCocktailRecipeSideBar.setOnClickListener {
                drawerLayoutDrawerCocktailRecipeDrawer.openDrawer(GravityCompat.END)
            }

            imageViewDrawerCocktailRecipeClose.setOnClickListener {
                drawerLayoutDrawerCocktailRecipeDrawer.closeDrawers()
            }

            includeDrawerCocktailRecipeInclude.textViewCocktailRecipeEnd.setOnClickListener {
                mainActivity.onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        if (::speechRecognizer.isInitialized) {
            startListening()
        }
    }

    override fun onPause() {
        super.onPause()
        if (::speechRecognizer.isInitialized) {
            try {
                speechRecognizer.stopListening()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainActivity.hideBottomNav(false)
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (::speechRecognizer.isInitialized) {
            try {
                speechRecognizer.destroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        _contentBinding = null
    }
}