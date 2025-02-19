package com.hontail.ui.ingredient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemIngredientAddBottomSheetUnitBinding
import com.hontail.ui.ingredient.screen.UnitItem

class IngredientAdapter(private val items: List<UnitItem>): RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder>() {

    lateinit var ingredientListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ListItemIngredientAddBottomSheetUnitBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IngredientViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    inner class IngredientViewHolder(private val binding: ListItemIngredientAddBottomSheetUnitBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UnitItem, position: Int) {

            binding.apply {

                textViewListItemIngredientAddBottomSheetUnitName.text = item.unitName

                if(item.unitSelected) {
                    imageViewListItemIngredientAddBottomSheetUnitCheck.visibility = View.VISIBLE
                }
                else {
                    imageViewListItemIngredientAddBottomSheetUnitCheck.visibility = View.INVISIBLE
                }

                root.setOnClickListener {
                    ingredientListener.onClick(position)
                }
            }
        }
    }
}