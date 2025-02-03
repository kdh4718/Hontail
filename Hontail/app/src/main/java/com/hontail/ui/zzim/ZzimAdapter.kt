package com.hontail.ui.zzim

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.FragmentZzimCardBinding

class ZzimAdapter(
    private val context: Context,
    private var zzimList: List<ZzimItem>
) : RecyclerView.Adapter<ZzimAdapter.ZzimHolder>() {

    inner class ZzimHolder(private val binding: FragmentZzimCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ZzimItem) {
            // 여기에 데이터 바인딩 로직 추가
            // 예: binding.titleTextView.text = item.title

            itemView.setOnClickListener {
                // 클릭 이벤트 처리
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

    // 데이터 업데이트를 위한 함수 추가
    fun updateData(newList: List<ZzimItem>) {
        zzimList = newList
        notifyDataSetChanged()
    }
}