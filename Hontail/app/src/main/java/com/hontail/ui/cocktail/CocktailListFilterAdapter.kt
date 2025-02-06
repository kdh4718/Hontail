package com.hontail.ui.cocktail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailListFilterItemBinding

class CocktailListFilterAdapter(private val items: List<String>): RecyclerView.Adapter<CocktailListFilterAdapter.CocktailListFilterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailListFilterViewHolder {
        val binding = ListItemCocktailListFilterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailListFilterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CocktailListFilterViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class CocktailListFilterViewHolder(private val binding: ListItemCocktailListFilterItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String) {

            binding.apply {

                textViewListItemCocktailListFilterItem.text = item
            }
        }
    }
}