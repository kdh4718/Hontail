package com.hontail.ui.zzim

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.FragmentZzimCardBinding

class ZzimAdapter(private val context: Context, private var zzimList: List<ZzimItem>) :
    RecyclerView.Adapter<ZzimAdapter.ZzimHolder>() {

    inner class ZzimHolder(private val binding: FragmentZzimCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ZzimItem) {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZzimHolder {
        val binding = FragmentZzimCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ZzimHolder(binding)
    }

    override fun onBindViewHolder(holder: ZzimHolder, position: Int) {
        holder.bind(zzimList[position])
    }

    override fun getItemCount(): Int {
        return zzimList.size
    }
}
