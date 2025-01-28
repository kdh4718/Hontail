package com.hontail.ui.mypage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailBinding
import com.hontail.databinding.ListItemMypageProfileBinding

class MyPageCocktailAdapter(private val items: List<MyPageItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var myPageProfileListener: ItemOnClickListener
    lateinit var myPageIngredientListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClick()
    }

    companion object {
        const val VIEW_TYPE_PROFILE = 0
        const val VIEW_TYPE_COCKTAIL = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is MyPageItem.Profile -> VIEW_TYPE_PROFILE
            is MyPageItem.Cocktail -> VIEW_TYPE_COCKTAIL
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {

            VIEW_TYPE_PROFILE -> {
                val binding = ListItemMypageProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyPageProfileViewHolder(binding)
            }

            VIEW_TYPE_COCKTAIL -> {
                val binding = ListItemCocktailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyPageCocktailViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is MyPageProfileViewHolder -> holder.bind(items[position] as MyPageItem.Profile)
            is MyPageCocktailViewHolder -> holder.bind(items[position] as MyPageItem.Cocktail)
        }
    }

    // 프로필 ViewHolder
    inner class MyPageProfileViewHolder(private val binding: ListItemMypageProfileBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyPageItem.Profile) {

            binding.apply {
                textViewMyPageNickname.text = item.userName
                textViewMyPageCocktailCount.text = "레시피 ${item.recipeCnt}"

                // 프로필 관리
                buttonProfileManagement.setOnClickListener {
                    myPageProfileListener.onClick()
                }

                // 재료 요청
                buttonRequestMaterial.setOnClickListener {
                    myPageIngredientListener.onClick()
                }
            }
        }
    }

    // 나만의 칵테일 ViewHolder
    inner class MyPageCocktailViewHolder(private val binding: ListItemCocktailBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(item: MyPageItem.Cocktail) {

            binding.apply {
                textViewListItemCocktailName.text = item.cocktailName
                textViewListItemCocktailBaseSpirit.text = item.cocktailBaseSpirit
                textViewListItemCocktailIngredientCount.text = "재료 ${item.cocktailIngredientCnt}개"
                textViewListItemCocktailTotalZzim.text = item.cocktailZzimCnt.toString()
                textViewListItemCocktailAlcoholContent.text = "${item.cocktailAlcholContent}%"
            }
        }
    }

}