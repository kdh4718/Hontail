package com.hontail.ui.picture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemPictureTopBinding

class PictureTopAdapter : RecyclerView.Adapter<PictureTopAdapter.TopViewHolder>() {
    private var item: PictureResultItem.TopItem? = null

    inner class TopViewHolder(private val binding: ListItemPictureTopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PictureResultItem.TopItem) {
            binding.textViewPictureResultSuggestion.text = item.suggestion
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopViewHolder {
        return TopViewHolder(
            ListItemPictureTopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TopViewHolder, position: Int) {
        item?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = if (item != null) 1 else 0

    fun setItem(newItem: PictureResultItem.TopItem) {
        item = newItem
        notifyDataSetChanged()
    }
}