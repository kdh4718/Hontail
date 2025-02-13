package com.hontail.ui.cocktail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hontail.R
import com.hontail.data.model.response.Recipe
import com.hontail.databinding.ListItemCocktailRecipeDrawerBinding

class CocktailRecipeDrawerAdapter(
    private val recipes: List<Recipe>
) : RecyclerView.Adapter<CocktailRecipeDrawerAdapter.ViewHolder>() {

    private var selectedPosition = 0  // 현재 선택된 위치 추적
    private var onItemClickListener: ((Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    inner class ViewHolder(private val binding: ListItemCocktailRecipeDrawerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != selectedPosition) {
                    val oldPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(oldPosition)
                    notifyItemChanged(selectedPosition)
                    onItemClickListener?.invoke(position)
                }
            }
        }

        fun bind(recipe: Recipe) {
            binding.apply {
                textViewCocktailRecipeDrawerNumber.text = recipe.sequence.toString()
                textViewCocktailRecipeDrawerExplanation.text = recipe.recipeGuide

                // 선택된 항목에 하이라이트 적용
                if (bindingAdapterPosition == selectedPosition) {
                    root.setBackgroundColor(ContextCompat.getColor(root.context, R.color.basic_gray))
                } else {
                    root.setBackgroundColor(ContextCompat.getColor(root.context, android.R.color.transparent))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemCocktailRecipeDrawerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = recipes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(recipes[position])
    }

    // ViewPager에서 페이지가 변경될 때 호출할 메서드
    fun updateSelectedPosition(position: Int) {
        val oldPosition = selectedPosition
        selectedPosition = position
        notifyItemChanged(oldPosition)
        notifyItemChanged(selectedPosition)
    }
}