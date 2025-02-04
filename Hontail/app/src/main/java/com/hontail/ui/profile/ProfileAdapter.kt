package com.hontail.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailBinding
import com.hontail.databinding.ListItemProfileEmptyBinding
import com.hontail.databinding.ListItemProfileProfileBinding
import com.hontail.ui.mypage.MyPageCocktailAdapter
import com.hontail.util.CommonUtils

class ProfileAdapter(private val items: List<ProfileItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_PROFILE = 0
        const val VIEW_TYPE_COCKTAIL = 1
        const val VIEW_TYPE_EMPTY = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is ProfileItem.Profile -> MyPageCocktailAdapter.VIEW_TYPE_PROFILE
            is ProfileItem.Cocktail -> MyPageCocktailAdapter.VIEW_TYPE_COCKTAIL
            is ProfileItem.Empty -> MyPageCocktailAdapter.VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {

            ProfileAdapter.VIEW_TYPE_PROFILE -> {
                val binding = ListItemProfileProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProfileProfileViewHolder(binding)
            }

            ProfileAdapter.VIEW_TYPE_COCKTAIL -> {
                val binding = ListItemCocktailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProfileCocktailViewHolder(binding)
            }

            ProfileAdapter.VIEW_TYPE_EMPTY -> {
                val binding = ListItemProfileEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ProfileEmptyViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is ProfileProfileViewHolder -> holder.bind(items[position] as ProfileItem.Profile)
            is ProfileCocktailViewHolder -> holder.bind(items[position] as ProfileItem.Cocktail)
            is ProfileEmptyViewHolder -> holder.bind()
        }
    }

    // 프로필 ViewHolder
    inner class ProfileProfileViewHolder(private val binding: ListItemProfileProfileBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProfileItem.Profile) {

            binding.apply {
                textViewProfileNickname.text = item.userName
                textViewProfileCocktailCount.text = "레시피 ${item.recipeCnt}"
            }
        }
    }

    // 상대방 나만의 칵테일 ViewHolder
    inner class ProfileCocktailViewHolder(private val binding: ListItemCocktailBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProfileItem.Cocktail) {

            binding.apply {
                textViewListItemCocktailName.text = item.cocktailName
                textViewListItemCocktailBaseSpirit.text = item.cocktailBaseSpirit
                textViewListItemCocktailIngredientCount.text = "재료 ${item.cocktailIngredientCnt}개"
                textViewListItemCocktailTotalZzim.text = CommonUtils.makeComma(item.cocktailZzimCnt)
                textViewListItemCocktailAlcoholContent.text = "${item.cocktailAlcholContent}%"
            }
        }
    }

    // 비어있을 때 ViewHolder
    inner class ProfileEmptyViewHolder(private val binding: ListItemProfileEmptyBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            binding.apply {
                textViewListItemProfileEmptyTitle.text = "등록된 칵테일이 없어요."
            }
        }
    }
}