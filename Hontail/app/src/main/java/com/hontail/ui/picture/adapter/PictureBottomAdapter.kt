package com.hontail.ui.picture.adapter

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemPictureBottomBinding
import com.hontail.ui.MainActivity
import com.hontail.ui.cocktail.CocktailListFilterAdapter
import com.hontail.ui.mypage.Cocktail
import com.hontail.ui.picture.screen.CocktailPictureResultFragment
import com.hontail.ui.picture.screen.FilterBottomSheetFragment
import com.hontail.util.CocktailItemAdapter

class PictureBottomAdapter(
    private val context: Context,
    private var data: CocktailPictureResultFragment.PictureResultType.Bottom
) : RecyclerView.Adapter<PictureBottomAdapter.ViewHolder>() {

    // GridSpacing 클래스 추가
    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount
        }
    }

    inner class ViewHolder(private val binding: ListItemPictureBottomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CocktailPictureResultFragment.PictureResultType.Bottom) {
            binding.textViewPictureResultCocktailList.text = item.cocktailCount

            // 필터 리사이클러뷰 설정
            val filters = listOf("찜", "시간", "도수", "베이스주")
            val filterAdapter = CocktailListFilterAdapter(filters)

            binding.recyclerViewPictureResultFilter.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = filterAdapter
            }

            // 칵테일 리스트 리사이클러뷰 설정
            val cocktails = mutableListOf<Cocktail>().apply {
                repeat(10) {
                    add(Cocktail("깔루아 밀크", "리큐어", 2, 1231, 5))
                }
            }

            val cocktailAdapter = CocktailItemAdapter(cocktails)

            binding.recyclerViewPictureResultCocktailList.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = cocktailAdapter
                // ItemDecoration 추가
                if (itemDecorationCount == 0) {
                    addItemDecoration(GridSpacingItemDecoration(2, 20))
                }
            }

            // 필터 클릭 이벤트 설정
            filterAdapter.cocktailListFilterListener = object : CocktailListFilterAdapter.ItemOnClickListener {
                override fun onClickFilter(position: Int) {
                    val bottomSheetFragment = FilterBottomSheetFragment.newInstance(position)
                    bottomSheetFragment.show(
                        (context as MainActivity).supportFragmentManager,
                        bottomSheetFragment.tag
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemPictureBottomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data)
    }

    override fun getItemCount(): Int = 1
}