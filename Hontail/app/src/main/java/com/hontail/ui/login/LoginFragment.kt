package com.hontail.ui.login

import android.view.View
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.viewpager2.widget.ViewPager2
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentLoginBinding

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = LoginPagerAdapter(this)
        binding.viewPagerLoginImage.adapter = adapter
        binding.viewPagerLoginImage.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPagerLoginImage.setCurrentItem(0, true)  // 무한 스크롤 효과를 위해 초기값 설정

        // 인디케이터 연결
        binding.indicatorLoginIndicator.setViewPager(binding.viewPagerLoginImage)

        // 자동 슬라이드 시작
        startAutoSlide()

        // 페이지 변경 리스너 추가
        binding.viewPagerLoginImage.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                handler.removeCallbacks(autoScrollRunnable)
                startAutoSlide()
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