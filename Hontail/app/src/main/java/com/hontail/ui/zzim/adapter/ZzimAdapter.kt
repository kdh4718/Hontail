package com.hontail.ui.zzim.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.FragmentZzimCardBinding
import com.hontail.ui.zzim.screen.ZzimItem

class ZzimAdapter(
    private val context: Context,
    private var zzimList: List<ZzimItem>
) : RecyclerView.Adapter<ZzimAdapter.ZzimHolder>() {

    inner class ZzimHolder(private val binding: FragmentZzimCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ZzimItem) {

            itemView.setOnClickListener {
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZzimHolder {
        return ZzimHolder(
            FragmentZzimCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ZzimHolder, position: Int) {
        holder.bind(zzimList[position])
    }

    override fun getItemCount(): Int = zzimList.size

    fun updateData(newList: List<ZzimItem>) {
        zzimList = newList
        notifyDataSetChanged()
    }
}