package com.hontail.ui.login

import android.content.Context
import android.content.Intent
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
import com.hontail.ui.MainActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
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

    // 카카오계정으로 로그인 공통 callback 구성
    // 카카오톡으로 로그인 할 수 없어 카카오계정으로 로그인할 경우 사용됨
    val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            Log.i(TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
        }
    }

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

        viewModel.userId.observe(viewLifecycleOwner) {
            Log.d(TAG, "initLogin: $it") // 여기까지 정상 출력됨

            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("user_id", it?.toInt())
            }

            Log.d(TAG, "Intent Extra user_id: ${intent.getIntExtra("user_id", 0)}") // 여기서 10이 찍혀야 정상
            startActivity(intent)
        }

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
                loginWithKakao()
            }

            imageViewLoginNaver.setOnClickListener {
                loginWithNaver()
            }

            imageViewLoginGoogle.setOnClickListener {

            }
        }
    }

    fun loginWithKakao() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.d(TAG, "카카오톡으로 로그인 실패", error)

                    // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                    // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                    UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
                } else if (token != null) {
                    Log.d(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
                    viewModel.loginWithKakao(token.accessToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
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