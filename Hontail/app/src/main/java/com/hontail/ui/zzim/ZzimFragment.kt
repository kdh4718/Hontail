package com.hontail.ui.zzim

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.hontail.R
import com.hontail.databinding.FragmentZzimBinding
import com.hontail.ui.MainActivity

class ZzimFragment : Fragment(R.layout.fragment_zzim) {

    private lateinit var binding: FragmentZzimBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var zzimAdapter: ZzimAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentZzimBinding.bind(view)

        initAdapter()
    }

    private fun initAdapter() {
        val zzimList = listOf(
            ZzimItem("아이템 1"),
            ZzimItem("아이템 2"),
            ZzimItem("아이템 3"),
            ZzimItem("아이템 4"),
            ZzimItem("아이템 5"),
            ZzimItem("아이템 6"),
            ZzimItem("아이템 7"),
            ZzimItem("아이템 8"),
            ZzimItem("아이템 9"),
            ZzimItem("아이템 10")
        )

        zzimAdapter = ZzimAdapter(requireContext(), zzimList)

        binding.recyclerViewZzim.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = zzimAdapter
        }
    }
}

data class ZzimItem(
    val title: String
)
