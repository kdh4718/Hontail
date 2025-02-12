package com.hontail.util

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hontail.R
import com.hontail.data.model.response.CocktailListResponse
import com.hontail.databinding.ListItemCocktailBinding

class CocktailItemAdapter(
    private val context: Context
) : PagingDataAdapter<CocktailListResponse, CocktailItemAdapter.CocktailItemViewHolder>(DIFF_CALLBACK) {

    lateinit var cocktailItemListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickCocktailItem(cocktailId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailItemViewHolder {
        val binding = ListItemCocktailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CocktailItemViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class CocktailItemViewHolder(private val binding: ListItemCocktailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CocktailListResponse) {
            binding.apply {
                textViewListItemCocktailName.text = item.cocktailName
                textViewListItemCocktailBaseSpirit.text = item.baseSpirit
                textViewListItemCocktailIngredientCount.text = "재료 ${item.ingredientCount}개"
                textViewListItemCocktailTotalZzim.text = item.likes.toString()
                textViewListItemCocktailAlcoholContent.text = "${item.alcoholContent}%"
                
                Glide.with(context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_bottom_navi_zzim_selected)
                    .error(R.drawable.ic_bottom_navi_zzim_unselected)
                    .into(imageViewListItemCocktailCocktail)

                imageViewListItemCocktailZzim.setImageDrawable(
                    ContextCompat.getDrawable(context, if (item.isLiked) R.drawable.ic_bottom_navi_zzim_selected else R.drawable.ic_bottom_navi_zzim_unselected)
                )

                root.setOnClickListener {
                    cocktailItemListener.onClickCocktailItem(item.id)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CocktailListResponse>() {
            override fun areItemsTheSame(oldItem: CocktailListResponse, newItem: CocktailListResponse): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: CocktailListResponse, newItem: CocktailListResponse): Boolean {
                return oldItem == newItem
            }
        }
    }
}
