package com.hontail.ui.custom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCustomCocktailSearchBarBinding
import com.hontail.databinding.ListItemCustomCocktailSearchHeaderBinding
import com.hontail.databinding.ListItemCustomCocktailSearchResultBinding

class CustomCocktailSearchAdapter(private val items: List<CustomCocktailSearchItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var customCocktailSearchCancelListener: ItemOnClickListener
    lateinit var customCocktailSearchIngredientListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClick(position: Int?)
    }

    companion object {
        const val VIEW_TYPE_SEARCH_BAR = 0
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_INGREDIENT_RESULT = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is CustomCocktailSearchItem.SearchBar -> VIEW_TYPE_SEARCH_BAR
            is CustomCocktailSearchItem.Header -> VIEW_TYPE_HEADER
            is CustomCocktailSearchItem.IngredientResult -> VIEW_TYPE_INGREDIENT_RESULT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {

            VIEW_TYPE_SEARCH_BAR -> {
                val binding = ListItemCustomCocktailSearchBarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailSearchBarViewHolder(binding)
            }

            VIEW_TYPE_HEADER -> {
                val binding = ListItemCustomCocktailSearchHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailSearchHeaderViewHolder(binding)
            }

            VIEW_TYPE_INGREDIENT_RESULT -> {
                val binding = ListItemCustomCocktailSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CustomCocktailSearchIngredientResultViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder) {

            is CustomCocktailSearchBarViewHolder -> holder.bind(items[position] as CustomCocktailSearchItem.SearchBar)
            is CustomCocktailSearchHeaderViewHolder -> holder.bind()
            is CustomCocktailSearchIngredientResultViewHolder -> holder.bind(items[position] as CustomCocktailSearchItem.IngredientResult)
        }
    }

    // search bar
    inner class CustomCocktailSearchBarViewHolder(private val binding: ListItemCustomCocktailSearchBarBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomCocktailSearchItem.SearchBar) {

            binding.apply {

                textViewListItemCustomCocktailSearchBarCancel.setOnClickListener {
                    customCocktailSearchCancelListener.onClick(null)
                }
            }
        }
    }

    // header
    inner class CustomCocktailSearchHeaderViewHolder(private val binding: ListItemCustomCocktailSearchHeaderBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            binding.apply {

            }
        }
    }

    // Ingredient Result
    inner class CustomCocktailSearchIngredientResultViewHolder(private val binding: ListItemCustomCocktailSearchResultBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CustomCocktailSearchItem.IngredientResult) {

            binding.apply {

                textViewListItemCustomCocktailSearchResultIngredientName.text = item.ingredientName

                root.setOnClickListener {
                    customCocktailSearchIngredientListener.onClick(0)
                }
            }
        }
    }
}