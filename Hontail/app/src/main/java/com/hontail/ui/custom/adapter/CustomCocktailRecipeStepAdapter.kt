package com.hontail.ui.custom.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCustomCocktailRecipeStepItemBinding
import com.hontail.ui.custom.screen.CocktailRecipeStep

class CustomCocktailRecipeStepAdapter(private var items: MutableList<CocktailRecipeStep>): RecyclerView.Adapter<CustomCocktailRecipeStepAdapter.CustomCocktailRecipeStepViewHolder>() {

    lateinit var customCocktailRecipeStepListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickDelete(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCocktailRecipeStepViewHolder {
        val binding = ListItemCustomCocktailRecipeStepItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomCocktailRecipeStepViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CustomCocktailRecipeStepViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    inner class CustomCocktailRecipeStepViewHolder(private val binding: ListItemCustomCocktailRecipeStepItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CocktailRecipeStep, position: Int) {

            binding.apply {

                if(item.stepNumber == 1) {
                    imageViewListItemCustomCocktailRecipeStepDelete.visibility = View.GONE
                }
                else {
                    imageViewListItemCustomCocktailRecipeStepDelete.visibility = View.VISIBLE
                }

                textViewListItemCustomCocktailRecipeStepSequence.text = item.stepNumber.toString()

                imageViewListItemCustomCocktailRecipeStepDelete.setOnClickListener {
                    customCocktailRecipeStepListener.onClickDelete(position)
                }
            }
        }
    }
}