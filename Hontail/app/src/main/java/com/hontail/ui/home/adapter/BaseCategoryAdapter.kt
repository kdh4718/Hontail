package com.hontail.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemHomeCategoryCategoryBinding
import com.hontail.ui.home.screen.HomeCategoryItem

class BaseCategoryAdapter(val context: Context, var categoryList: List<HomeCategoryItem>) :
    RecyclerView.Adapter<BaseCategoryAdapter.BaseCategoryHolder>() {

    lateinit var cocktailBaseCategoryListener: ItemOnClickListener

    interface ItemOnClickListener{
        fun onClickCategory(name: String)
    }

    inner class BaseCategoryHolder(private val binding: ListItemHomeCategoryCategoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeCategoryItem) {
            binding.apply {
                textViewHomeCategoryExplanation.text = item.explanation
                textViewHomeCategoryName.text = item.name
                imageViewHomeCategory.setImageResource(item.imageRes)

                root.setOnClickListener{
                    cocktailBaseCategoryListener.onClickCategory(item.name)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseCategoryHolder {
        val binding = ListItemHomeCategoryCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BaseCategoryHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseCategoryHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}