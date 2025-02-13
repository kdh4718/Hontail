package com.hontail.ui.picture.adapter

import android.content.Context
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemPictureBottomBinding
import com.hontail.ui.picture.screen.CocktailPictureResultFragment
import com.hontail.util.CocktailItemAdapter

private const val TAG = "PictureBottomAdapter_SSAFY"

class PictureBottomAdapter(
    private val context: Context,
    private var data: CocktailPictureResultFragment.PictureResultType.Bottom
) : RecyclerView.Adapter<PictureBottomAdapter.ViewHolder>() {

    lateinit var pictureBottomListener: ItemClickListener

    interface ItemClickListener {
        fun onClickCocktailItem(cocktailId: Int)
    }

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
            binding.textViewPictureResultCocktailList.text = "칵테일 " + item.cocktailCount + "개"

            val cocktailAdapter = CocktailItemAdapter(context, item.cocktails)

            binding.recyclerViewPictureResultCocktailList.apply {
                layoutManager = GridLayoutManager(context, 2)
                adapter = cocktailAdapter
                // ItemDecoration 추가
                if (itemDecorationCount == 0) {
                    addItemDecoration(GridSpacingItemDecoration(2, 20))

                }

                cocktailAdapter.cocktailItemListener =
                    object : CocktailItemAdapter.ItemOnClickListener {
                        override fun onClickCocktailItem(cocktailId: Int) {
                            pictureBottomListener.onClickCocktailItem(cocktailId)
                        }

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

    fun updateData(newData: CocktailPictureResultFragment.PictureResultType.Bottom) {
        Log.d(TAG, "AI updateData: ${newData}")
        data = newData
        notifyDataSetChanged()
    }
}