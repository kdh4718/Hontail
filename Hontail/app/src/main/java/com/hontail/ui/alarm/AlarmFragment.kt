package com.hontail.ui.alarm

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentAlarmBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class AlarmFragment : BaseFragment<FragmentAlarmBinding>(
    FragmentAlarmBinding::bind,
    R.layout.fragment_alarm
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()
    private val alarmAdapter = AlarmAdapter()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupAlarmList()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewAlarms.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alarmAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupAlarmList() {
        val alarmItems = mutableListOf<AlarmItem>().apply {
            // Add setting item at the top
            add(AlarmItem.Setting())

            // Add alarm items
            addAll(getDummyAlarms())
        }

        alarmAdapter.submitList(alarmItems)
    }

    private fun getDummyAlarms(): List<AlarmItem.AlarmContent> = listOf(
        AlarmItem.AlarmContent("[공지사항] 혼테일 알림창 테스트 1", "2025.01.24", R.drawable.cocktail_sample),
        AlarmItem.AlarmContent("[업데이트] 새로운 기능 추가", "2025.01.25", R.drawable.cocktail_sample),
        AlarmItem.AlarmContent("[이벤트] 특별 할인 행사", "2025.01.26", R.drawable.cocktail_sample),
        AlarmItem.AlarmContent("[공지사항] 서버 점검 안내", "2025.01.27", R.drawable.cocktail_sample),
        AlarmItem.AlarmContent("[이벤트] 신규 칵테일 레시피 공개", "2025.01.28", R.drawable.cocktail_sample),
        AlarmItem.AlarmContent("[안내] 앱 사용 가이드 업데이트", "2025.01.29", R.drawable.cocktail_sample),
        AlarmItem.AlarmContent("[이벤트] 주간 인기 칵테일 TOP 10", "2025.01.30", R.drawable.cocktail_sample),
        AlarmItem.AlarmContent("[공지사항] 커뮤니티 이용 수칙 안내", "2025.01.31", R.drawable.cocktail_sample),
        AlarmItem.AlarmContent("[업데이트] 버그 수정 및 안정성 개선", "2025.02.01", R.drawable.cocktail_sample),
        AlarmItem.AlarmContent("[이벤트] 2월 시즌 한정 칵테일 출시", "2025.02.02", R.drawable.cocktail_sample)
    )
}

sealed class AlarmItem {
    data class Setting(
        val title1: String = "기기 알림을 켜주세요",
        val title2: String = "소식을 앱 푸시로 받아보세요"
    ) : AlarmItem()

    data class AlarmContent(
        val title: String,
        val date: String,
        val imageResId: Int
    ) : AlarmItem()
}