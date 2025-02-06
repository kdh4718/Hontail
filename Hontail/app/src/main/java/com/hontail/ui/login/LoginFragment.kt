package com.hontail.ui.login

import android.content.Context
import android.view.View
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentLoginBinding
import com.hontail.ui.LoginActivity
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback

private const val TAG = "LoginFragment_SSAFY"

class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::bind,
    R.layout.fragment_login
) {
    private lateinit var adapter: LoginPagerAdapter
    private val handler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = Runnable {
        val nextItem = (binding.viewPagerLoginImage.currentItem + 1) % adapter.itemCount
        binding.viewPagerLoginImage.setCurrentItem(nextItem, true)
    }

    private val viewModel: LoginFragmentViewModel by viewModels()

    private lateinit var loginActivity: LoginActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loginActivity = context as LoginActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViewPager()
        initLogin()
        initEvent()
    }

    fun initViewPager() {
        adapter = LoginPagerAdapter(this)

        binding.apply {
            viewPagerLoginImage.adapter = adapter
            viewPagerLoginImage.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPagerLoginImage.setCurrentItem(0, true)  // 무한 스크롤 효과를 위해 초기값 설정

            indicatorLoginIndicator.setViewPager(binding.viewPagerLoginImage)
        }

        // 자동 슬라이드 시작
        startAutoSlide()
    }

    fun initLogin() {
        NaverIdLoginSDK.initialize(requireContext(), "KY6PkSapyHMt232etHJz", "${NAVER_CLIENT_SECRET}", "hontail")
    }

    fun initEvent() {
        binding.apply {
            viewPagerLoginImage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    handler.removeCallbacks(autoScrollRunnable)
                    startAutoSlide()
                }
            })

            imageViewLoginKakao.setOnClickListener {

            }

            imageViewLoginNaver.setOnClickListener {
                loginWithNaver()
            }

            imageViewLoginGoogle.setOnClickListener {

            }
        }
    }

    fun loginWithNaver() {
        NaverIdLoginSDK.authenticate(requireContext(), object : OAuthLoginCallback {
            override fun onSuccess() {
                val accessToken = NaverIdLoginSDK.getAccessToken()
                Log.d(TAG, "NaverLogin onSuccess: $accessToken")

                if (accessToken != null) {
                    viewModel.loginWithNaver(accessToken)
                }
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Log.d(TAG, "NaverLogin onFailure: $message")
            }

            override fun onError(errorCode: Int, message: String) {
                Log.d(TAG, "NaverLogin onError: $message")
            }
        })
    }

    private fun startAutoSlide() {
        handler.postDelayed(autoScrollRunnable, 3000) // 3초마다 자동 슬라이드
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(autoScrollRunnable) // 메모리 누수 방지
    }
}