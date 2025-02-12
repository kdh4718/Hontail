package com.hontail.ui.cocktail.screen


import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.hontail.R
import com.hontail.base.BaseBottomSheetFragment
import com.hontail.databinding.FragmentCocktailCommentBottomSheetBinding
import com.hontail.ui.cocktail.adapter.CocktailCommentAdapter

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

            cocktailCommentAdapter = CocktailCommentAdapter(requireContext(), items).apply {
                actionListener = object : CocktailCommentAdapter.CommentActionListener {
                    override fun onEditComment(comment: Comment) {
                        // 수정 동작 구현
                    }

                    override fun onDeleteComment(comment: Comment) {
                        // 삭제 동작 구현
                    }
                }
            }

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