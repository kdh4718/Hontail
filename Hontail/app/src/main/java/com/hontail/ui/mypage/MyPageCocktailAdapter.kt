package com.hontail.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailBinding

class MyPageCocktailAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ListItemCocktailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPageCocktailViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 10
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    inner class MyPageCocktailViewHolder(private val binding: ListItemCocktailBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind() {

        }
    }

}