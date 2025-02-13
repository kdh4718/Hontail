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
            viewPagerLoginImage.setCurrentItem(0, true)

            indicatorLoginIndicator.setViewPager(binding.viewPagerLoginImage)
        }

        startAutoSlide()
    }

    fun initLogin() {
        NaverIdLoginSDK.initialize(requireContext(), "KY6PkSapyHMt232etHJz", "${NAVER_CLIENT_SECRET}", "hontail")

        viewModel.isUserDataReady.observe(viewLifecycleOwner) { isReady ->
            if (isReady) {
                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    putExtra("user_id", viewModel.userId.value?.toInt())
                    putExtra("user_nickname", viewModel.userNickname.value)
                }

                Log.d(TAG, "Intent Extra user_id: ${intent.getIntExtra("user_id", 0)}, user_nickname: ${intent.getStringExtra("user_nickname")}")
                startActivity(intent)

            }
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

            // ✅ 비회원으로 시작하기 버튼 클릭 시 MainActivity로 이동
            textViewLoginNonMember.setOnClickListener {
                moveToMainActivity()
            }
        }
    }

    fun loginWithKakao() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext()) { token, error ->
                if (error != null) {
                    Log.d(TAG, "카카오톡으로 로그인 실패", error)

                    if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                        return@loginWithKakaoTalk
                    }

                    UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
                } else if (token != null) {
                    Log.d(TAG, "카카오톡으로 로그인 성공 ${token.accessToken}")
//                    viewModel.loginWithKakao(token.accessToken)
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
        handler.postDelayed(autoScrollRunnable, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(autoScrollRunnable)
    }

    // ✅ MainActivity로 이동하는 함수
    private fun moveToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // LoginActivity 종료
    }
}
