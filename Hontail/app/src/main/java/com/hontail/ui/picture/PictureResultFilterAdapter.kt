package com.hontail.ui.picture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemPictureResultFilterBinding

class PictureResultFilterAdapter(private val filters: List<String>) :
    RecyclerView.Adapter<PictureResultFilterAdapter.FilterViewHolder>() {

    inner class FilterViewHolder(private val binding: ListItemPictureResultFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(filter: String) {
            binding.textViewPictureResultFilterItem.text = filter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        return FilterViewHolder(
            ListItemPictureResultFilterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(filters[position])
    }

    override fun getItemCount(): Int = filters.size
}