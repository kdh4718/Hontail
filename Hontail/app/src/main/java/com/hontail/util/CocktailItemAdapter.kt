package com.hontail.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.hontail.R
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.ListItemCocktailBinding

class CocktailItemAdapter(private val context: Context, private val items: List<CocktailListResponse>): RecyclerView.Adapter<CocktailItemAdapter.CocktailItemViewHolder>() {

    lateinit var cocktailItemListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickCocktailItem()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailItemViewHolder {
        val binding = ListItemCocktailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CocktailItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class CocktailItemViewHolder(private val binding: ListItemCocktailBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CocktailListResponse) {

            binding.apply {
                textViewListItemCocktailName.text = item.cocktailName
                textViewListItemCocktailBaseSpirit.text = item.baseSpirit
                textViewListItemCocktailIngredientCount.text = "재료 ${item.ingredientCount}개"
                textViewListItemCocktailTotalZzim.text = CommonUtils.makeComma(item.likes)
                textViewListItemCocktailAlcoholContent.text = "${item.alcoholContent}%"

                if (item.isLiked){
                    imageViewListItemCocktailZzim.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_bottom_navi_zzim_selected)
                    )
                    imageViewListItemCocktailZzim.setColorFilter(
                        ContextCompat.getColor(context, R.color.basic_pink),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }else{
                    imageViewListItemCocktailZzim.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_bottom_navi_zzim_unselected)
                    )
                }

                root.setOnClickListener {
                    cocktailItemListener.onClickCocktailItem()
                }
            }
        }
    }

}