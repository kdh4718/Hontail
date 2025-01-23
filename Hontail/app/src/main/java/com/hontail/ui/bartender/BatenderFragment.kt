package com.hontail.ui.bartender

import android.content.Context
import androidx.fragment.app.activityViewModels
import com.hontail.R
import com.hontail.base.BaseFragment
import com.hontail.databinding.FragmentBatenderBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.MainActivityViewModel

class BatenderFragment : BaseFragment<FragmentBatenderBinding>(
    FragmentBatenderBinding::bind,
    R.layout.fragment_batender
) {
    private lateinit var mainActivity: MainActivity
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }
}