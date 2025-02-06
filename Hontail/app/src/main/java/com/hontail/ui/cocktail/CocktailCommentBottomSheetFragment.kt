package com.hontail.ui.cocktail


import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentCocktailCommentBottomSheetBinding

class CocktailCommentBottomSheetFragment : BaseBottomSheetFragment<FragmentCocktailCommentBottomSheetBinding>(
    FragmentCocktailCommentBottomSheetBinding::bind,
    R.layout.fragment_cocktail_comment_bottom_sheet
) {
    private lateinit var cocktailCommentAdapter: CocktailCommentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
    }

    fun initAdapter() {

        binding.apply {

            val items = listOf(
                Comment(1, "name1", "comment1", R.drawable.add_ingredient),
                Comment(2, "name2", "comment2", R.drawable.add_ingredient),
                Comment(3, "name3", "comment3", R.drawable.add_ingredient),
                Comment(4, "name4", "comment4", R.drawable.add_ingredient),
                Comment(5, "name4", "comment4", R.drawable.add_ingredient),
                Comment(6, "name4", "comment4", R.drawable.add_ingredient),
                Comment(7, "name4", "comment4", R.drawable.add_ingredient),
            )

            cocktailCommentAdapter = CocktailCommentAdapter(requireContext(), items)

            recyclerViewCocktailCommentBottomSheet.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = cocktailCommentAdapter
            }
        }

    }
}

data class Comment(
    val id: Int,
    val name: String,
    val comment: String,
    val imageRes: Int
)