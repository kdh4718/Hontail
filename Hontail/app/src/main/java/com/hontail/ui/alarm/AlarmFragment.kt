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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyAlarms = listOf(
            Alarm("[공지사항] 혼테일 알림창 테스트 1", "2025.01.24", R.drawable.cocktail_sample),
            Alarm("[업데이트] 새로운 기능 추가", "2025.01.25", R.drawable.cocktail_sample),
            Alarm("[이벤트] 특별 할인 행사", "2025.01.26", R.drawable.cocktail_sample)
        )

        val alarmAdapter = AlarmAdapter(dummyAlarms)

        binding.recyclerViewAlarms.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = alarmAdapter
            setHasFixedSize(true)
        }
    }
}
