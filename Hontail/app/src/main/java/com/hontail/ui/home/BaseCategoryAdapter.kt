package com.hontail.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemHomeCategoryBinding

class BaseCategoryAdapter(val context: Context, var categoryList: List<HomeCategoryItem>) :
    RecyclerView.Adapter<BaseCategoryAdapter.BaseCategoryHolder>() {

    inner class BaseCategoryHolder(private val binding: ListItemHomeCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindInfo(item: HomeCategoryItem){
            binding.apply {
                textViewHomeCategoryExplanation.text = item.explanation
                textViewHomeCategoryName.text = item.name
                imageViewHomeCategory.setImageResource(item.imageRes)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCategoryHolder {
        val binding = ListItemHomeCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseCategoryHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseCategoryHolder, position: Int) {
        holder.bindInfo(categoryList[position])
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}