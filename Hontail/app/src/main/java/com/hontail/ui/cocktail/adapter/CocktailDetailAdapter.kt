package com.hontail.ui.cocktail.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hontail.R
import com.hontail.data.model.response.CocktailIngredient
import com.hontail.data.model.response.Recipe
import com.hontail.databinding.ListItemCocktailDetailInfosBinding
import com.hontail.databinding.ListItemCocktailDetailIngredientsBinding
import com.hontail.databinding.ListItemCocktailDetailRecipesBinding
import com.hontail.ui.cocktail.screen.CocktailDetailItem
import com.hontail.ui.cocktail.screen.CocktailListItem

private const val TAG = "CocktailDetailAdapter_SSAFY"

class CocktailDetailAdapter(private val context: Context, private var items: MutableList<CocktailDetailItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var cocktailDetailListener: ItemOnClickListener
    private var backButtonClickListener: (() -> Unit)? = null

    fun setBackButtonClickListener(listener: () -> Unit) {
        backButtonClickListener = listener
    }

    interface ItemOnClickListener {
        fun onClickRecipeBottomSheet()
        fun onClickCommentBottomSheet()
        fun onClickZzimButton(cocktailId: Int, isLiked: Boolean) // 좋아요 버튼 클릭 이벤트
        fun onClickModify()
        fun onClickDelete(cocktailId: Int)
    }

    companion object {
        const val VIEW_TYPE_COCKTAIL_DETAIL_INFOS = 0
        const val VIEW_TYPE_COCKTAIL_DETAIL_INGREDIENTS = 1
        const val VIEW_TYPE_COCKTAIL_DETAIL_RECIPES = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is CocktailDetailItem.CocktailInfo -> VIEW_TYPE_COCKTAIL_DETAIL_INFOS
            is CocktailDetailItem.IngredientList -> VIEW_TYPE_COCKTAIL_DETAIL_INGREDIENTS
            is CocktailDetailItem.RecipeList -> VIEW_TYPE_COCKTAIL_DETAIL_RECIPES
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_COCKTAIL_DETAIL_INFOS -> {
                val binding = ListItemCocktailDetailInfosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailDetailInfosViewHolder(binding)
            }

            VIEW_TYPE_COCKTAIL_DETAIL_INGREDIENTS -> {
                val binding = ListItemCocktailDetailIngredientsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailDetailIngredientsViewHolder(binding)
            }

            VIEW_TYPE_COCKTAIL_DETAIL_RECIPES -> {
                val binding = ListItemCocktailDetailRecipesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CocktailDetailRecipesViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = items[position]) {
            is CocktailDetailItem.CocktailInfo -> (holder as CocktailDetailInfosViewHolder).bind(item)
            is CocktailDetailItem.IngredientList -> (holder as CocktailDetailIngredientsViewHolder).bind(item.ingredients)
            is CocktailDetailItem.RecipeList -> (holder as CocktailDetailRecipesViewHolder).bind(item.recipes)
        }
    }

    // Cocktail Detail Infos
    inner class CocktailDetailInfosViewHolder(private val binding: ListItemCocktailDetailInfosBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CocktailDetailItem.CocktailInfo) {
            binding.apply {
                if (item.userId != 0){
                    imageViewCocktailDetailZzim.isEnabled = true
                }else{
                    imageViewCocktailDetailZzim.isEnabled = false
                }

                if (item.cocktailDetail.userId == item.userId){
                    imageViewCocktailDetailEdit.visibility = View.VISIBLE
                    imageViewCocktailDetailTrashCan.visibility = View.VISIBLE
                }else{
                    imageViewCocktailDetailEdit.visibility = View.GONE
                    imageViewCocktailDetailTrashCan.visibility = View.GONE
                }

                textViewCocktailDetailNameKor.text = item.cocktailDetail.cocktailName
                textViewCocktailDetailAlcoholLevel.text = "${item.cocktailDetail.alcoholContent}도"
                textViewCocktailDetailZzimCount.text = item.cocktailDetail.likeCnt.toString()
                textViewCocktailDetailCommentCount.text = item.cocktailDetail.commentCnt.toString()
                textViewListItemCocktailDetailInfosDescription.text = item.cocktailDetail.cocktailDescription

                Glide.with(context)
                    .load(item.cocktailDetail.imageUrl)
                    .into(imageViewCocktailDetailImage)

                if (item.cocktailDetail.isLiked){
                    imageViewCocktailDetailZzim.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_bottom_navi_zzim_selected)
                    )
                    imageViewCocktailDetailZzim.setColorFilter(
                        ContextCompat.getColor(context, R.color.basic_pink),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }else{
                    imageViewCocktailDetailZzim.setImageDrawable(
                        ContextCompat.getDrawable(context, R.drawable.ic_bottom_navi_zzim_unselected)
                    )
                }

                // 뒤로가기 버튼 클릭 리스너
                imageViewCocktailDetailGoBack.setOnClickListener {
                    backButtonClickListener?.invoke()
                }

                // 댓글 바텀시트 띄우기
                imageViewCocktailDetailComment.setOnClickListener {
                    cocktailDetailListener.onClickCommentBottomSheet()
                }

                // 좋아요 버튼 클릭 리스너 추가
                imageViewCocktailDetailZzim.setOnClickListener {
                    val newLikeStatus = !item.cocktailDetail.isLiked
                    item.cocktailDetail.isLiked = newLikeStatus
                    item.cocktailDetail.likeCnt = if (newLikeStatus) item.cocktailDetail.likeCnt + 1 else item.cocktailDetail.likeCnt - 1

                    updateZzimButton(newLikeStatus)

                    // 좋아요 상태 변경 이벤트를 프래그먼트로 전달
                    cocktailDetailListener.onClickZzimButton(item.cocktailDetail.cocktailId, newLikeStatus)
                    notifyDataSetChanged()
                }

                // 칵테일 수정하기.
                imageViewCocktailDetailEdit.setOnClickListener {
                    cocktailDetailListener.onClickModify()
                }

                // 칵테일 삭제하기.
                imageViewCocktailDetailTrashCan.setOnClickListener {
                    cocktailDetailListener.onClickDelete(item.cocktailDetail.cocktailId)
                }
            }
        }

        private fun updateZzimButton(isLiked: Boolean) {
            if (isLiked) {
                binding.imageViewCocktailDetailZzim.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_bottom_navi_zzim_selected)
                )
                binding.imageViewCocktailDetailZzim.setColorFilter(
                    ContextCompat.getColor(context, R.color.basic_pink),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            } else {
                binding.imageViewCocktailDetailZzim.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_bottom_navi_zzim_unselected)
                )
            }
        }
    }

    // Cocktail Detail Ingredients
    inner class CocktailDetailIngredientsViewHolder(private val binding: ListItemCocktailDetailIngredientsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(ingredients: List<CocktailIngredient>) {
            binding.apply {
                val cocktailDetailIngredientAdapter = CocktailDetailIngredientAdapter(context, ingredients)
                recyclerViewCocktailDetailIngredientIngredient.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerViewCocktailDetailIngredientIngredient.adapter = cocktailDetailIngredientAdapter
            }
        }
    }

    // Cocktail Detail Recipes
    inner class CocktailDetailRecipesViewHolder(private val binding: ListItemCocktailDetailRecipesBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(recipes: List<Recipe>) {
            binding.apply {
                val cocktailDetailRecipeAdapter = CocktailDetailRecipeAdapter(recipes)
                recyclerViewCocktailDetailRecipe.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                recyclerViewCocktailDetailRecipe.adapter = cocktailDetailRecipeAdapter

                imageViewCocktailDetailRecipeIcon.setOnClickListener {
                    cocktailDetailListener.onClickRecipeBottomSheet()
                }
            }
        }
    }

    fun updateItems(newItems: List<CocktailDetailItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged() // 전체 갱신
    }

    fun updateLikesCnt(){
        val index = items.indexOfFirst { it is CocktailDetailItem.CocktailInfo }
    }
}