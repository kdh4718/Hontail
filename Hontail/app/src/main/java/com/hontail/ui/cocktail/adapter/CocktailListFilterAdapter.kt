package com.hontail.ui.cocktail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailListFilterItemBinding

class CocktailListFilterAdapter(private val items: List<String>, private val selectedPosition: Int): RecyclerView.Adapter<CocktailListFilterAdapter.CocktailListFilterViewHolder>() {

    lateinit var cocktailListFilterListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickFilter(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailListFilterViewHolder {
        val binding = ListItemCocktailListFilterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailListFilterViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CocktailListFilterViewHolder, position: Int) {
        holder.bind(items[position], position, position == selectedPosition)
    }

    inner class CocktailListFilterViewHolder(private val binding: ListItemCocktailListFilterItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, position: Int, isSelected: Boolean) {
            binding.apply {
                textViewListItemCocktailListFilterItem.text = item
                root.isSelected = isSelected

                root.setOnClickListener {

                    cocktailListFilterListener.onClickFilter(position)
                }
            }
        }
    }
}