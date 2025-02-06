package com.hontail.ui.picture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemPictureBottomBinding

class PictureBottomAdapter : RecyclerView.Adapter<PictureBottomAdapter.BottomViewHolder>() {
    private var item: PictureResultItem.BottomItem? = null

    inner class BottomViewHolder(private val binding: ListItemPictureBottomBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PictureResultItem.BottomItem) {
            binding.textViewPictureResultCocktailList.text = item.cocktailCount
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomViewHolder {
        return BottomViewHolder(
            ListItemPictureBottomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BottomViewHolder, position: Int) {
        item?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = if (item != null) 1 else 0

    fun setItem(newItem: PictureResultItem.BottomItem) {
        item = newItem
        notifyDataSetChanged()
    }
}