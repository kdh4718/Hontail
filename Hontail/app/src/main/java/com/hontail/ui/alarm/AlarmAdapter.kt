package com.hontail.ui.alarm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemAlarmAlarmListBinding
import com.hontail.databinding.ListItemAlarmSettingAlertBinding

class AlarmAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<AlarmItem>()

    companion object {
        private const val VIEW_TYPE_SETTING = 0
        private const val VIEW_TYPE_ALARM = 1
    }

    class SettingViewHolder(
        private val binding: ListItemAlarmSettingAlertBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlarmItem.Setting) {
            binding.textViewAlarmAlert1.text = item.title1
            binding.textViewAlarmAlert2.text = item.title2
        }
    }

    class AlarmViewHolder(
        private val binding: ListItemAlarmAlarmListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AlarmItem.AlarmContent) {
            binding.textViewAlarmInfo.text = item.title
            binding.textViewAlarmDate.text = item.date
            binding.imageViewAlarmImage.setImageResource(item.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SETTING -> {
                val binding = ListItemAlarmSettingAlertBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SettingViewHolder(binding)
            }
            else -> {
                val binding = ListItemAlarmAlarmListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                binding.root.layoutParams = (binding.root.layoutParams as ViewGroup.MarginLayoutParams).apply {
                    topMargin = 76
                }
                AlarmViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is AlarmItem.Setting -> (holder as SettingViewHolder).bind(item)
            is AlarmItem.AlarmContent -> (holder as AlarmViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is AlarmItem.Setting -> VIEW_TYPE_SETTING
            is AlarmItem.AlarmContent -> VIEW_TYPE_ALARM
        }
    }

    fun submitList(newItems: List<AlarmItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}