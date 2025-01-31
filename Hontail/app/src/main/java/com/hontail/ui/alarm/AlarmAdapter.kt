package com.hontail.ui.alarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hontail.R

class AlarmAdapter(private val alarmList: List<Alarm>) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val alarmImage: ImageView = view.findViewById<CardView>(R.id.textViewAlarmImage)
            .findViewById(R.id.imageViewAlarmImage)
        val alarmTitle: TextView = view.findViewById(R.id.textViewAlarmInfo)
        val alarmDate: TextView = view.findViewById(R.id.textViewAlarmDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_alarm_card, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarmList[position]
        holder.alarmTitle.text = alarm.title
        holder.alarmDate.text = alarm.date
        holder.alarmImage.setImageResource(alarm.imageResId)
    }

    override fun getItemCount(): Int = alarmList.size
}