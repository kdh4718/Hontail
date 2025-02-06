package com.hontail.ui.mypage

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailBinding
import com.hontail.databinding.ListItemMypageCocktailBinding
import com.hontail.databinding.ListItemMypageEmptyBinding
import com.hontail.databinding.ListItemMypageProfileBinding
import com.hontail.util.CocktailItemAdapter

class MyPageAdapter(private val context: Context, private val items: List<MyPageItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    lateinit var myPageProfileListener: ItemOnClickListener
    lateinit var myPageIngredientListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClick()
    }

    companion object {
        const val VIEW_TYPE_PROFILE = 0
        const val VIEW_TYPE_COCKTAIL = 1
        const val VIEW_TYPE_EMPTY = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is MyPageItem.Profile -> VIEW_TYPE_PROFILE
            is MyPageItem.MyCocktail -> VIEW_TYPE_COCKTAIL
            is MyPageItem.Empty -> VIEW_TYPE_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {

            VIEW_TYPE_PROFILE -> {
                val binding = ListItemMypageProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyPageProfileViewHolder(binding)
            }

            VIEW_TYPE_COCKTAIL -> {
                val binding = ListItemMypageCocktailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyPageCocktailViewHolder(binding)
            }

            VIEW_TYPE_EMPTY -> {
                val binding = ListItemMypageEmptyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MyPageEmptyViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown ViewType: $viewType")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when(holder) {
//            is MyPageProfileViewHolder -> holder.bind(items[position] as MyPageItem.Profile)
//            is MyPageCocktailViewHolder -> (holder as MyPageCocktailViewHolder).bind(items)
//            is MyPageEmptyViewHolder -> holder.bind()
//        }
        when(val item = items[position]) {
            is MyPageItem.Profile -> (holder as MyPageProfileViewHolder).bind(item)
            is MyPageItem.MyCocktail -> (holder as MyPageCocktailViewHolder).bind(item.cocktailList)
            is MyPageItem.Empty -> (holder as MyPageEmptyViewHolder).bind()
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
    inner class MyPageCocktailViewHolder(private val binding: ListItemMypageCocktailBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(cocktailList: List<Cocktail>) {

            binding.apply {

                val myPageCocktailAdapter = CocktailItemAdapter(cocktailList)

                recyclerViewListItemMyPageCocktail.layoutManager = GridLayoutManager(context, 2)
                recyclerViewListItemMyPageCocktail.adapter = myPageCocktailAdapter
            }
        }
    }

    // 비어있을 때 ViewHolder
    inner class MyPageEmptyViewHolder(private val binding: ListItemMypageEmptyBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            binding.apply {
                textViewListItemMyPageEmptyTitle.text = "나만의 칵테일이 없어요."
                textViewListItemMyPageEmptySubTitle.text = "hyuun님만의 레시피를 등록해주세요."
            }
        }
    }

}