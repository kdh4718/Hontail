package com.hontail.ui.cocktail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailSearchRecentItemBinding

class CocktailSearchRecentAdapter(private val items: List<RecentItem>): RecyclerView.Adapter<CocktailSearchRecentAdapter.CocktailSearchRecentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailSearchRecentViewHolder {
        val binding = ListItemCocktailSearchRecentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailSearchRecentViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CocktailSearchRecentViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class CocktailSearchRecentViewHolder(private val binding: ListItemCocktailSearchRecentItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecentItem) {

            binding.apply {

                textViewListItemCocktailRecentItemName.text = item.cocktailName
            }
        }
    }
}