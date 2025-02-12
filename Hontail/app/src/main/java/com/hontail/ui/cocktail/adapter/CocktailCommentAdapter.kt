package com.hontail.ui.cocktail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hontail.databinding.ListItemCocktailCommentBinding
import com.hontail.ui.cocktail.screen.Comment

class CocktailCommentAdapter(
    private val context: Context,
    private var commentList: List<Comment>
) : RecyclerView.Adapter<CocktailCommentAdapter.CocktailCommentHolder>() {

    private val swipedItems = mutableSetOf<Int>()
    private val SWIPE_AMOUNT = -350f

    interface CommentActionListener {
        fun onEditComment(comment: Comment)
        fun onDeleteComment(comment: Comment)
    }

    var actionListener: CommentActionListener? = null

    inner class CocktailCommentHolder(private val binding: ListItemCocktailCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindInfo(item: Comment) {
            binding.apply {
                imageViewCocktailCommentUserImage.setImageResource(item.imageRes)
                textViewCocktailCommentUserName.text = item.name
                textViewCocktailCommentUserComment.text = item.comment

                // 초기 상태 설정
                contentContainer.translationX = if (swipedItems.contains(adapterPosition)) SWIPE_AMOUNT else 0f
                imageViewCocktailCommentUserComment.rotation = if (swipedItems.contains(adapterPosition)) 180f else 0f

                // 화살표 버튼 클릭 이벤트
                imageViewCocktailCommentUserComment.setOnClickListener {
                    if (swipedItems.contains(adapterPosition)) {
                        resetSwipe()
                    } else {
                        swipeItem()
                    }
                }

                // 수정/삭제 버튼 클릭 이벤트 - 콜백만 호출
                imageViewEdit.setOnClickListener {
                    actionListener?.onEditComment(item)
                }

                imageViewDelete.setOnClickListener {
                    actionListener?.onDeleteComment(item)
                }
            }
        }

        private fun swipeItem() {
            binding.apply {
                // 컨텐츠 스와이프 애니메이션
                contentContainer.animate()
                    .translationX(SWIPE_AMOUNT)
                    .setDuration(300)
                    .start()

                // 화살표 회전 애니메이션
                imageViewCocktailCommentUserComment.animate()
                    .rotation(180f)
                    .setDuration(300)
                    .start()
            }
            swipedItems.add(adapterPosition)
        }

        private fun resetSwipe() {
            binding.apply {
                // 컨텐츠 원위치 애니메이션
                contentContainer.animate()
                    .translationX(0f)
                    .setDuration(300)
                    .start()

                // 화살표 원위치 회전
                imageViewCocktailCommentUserComment.animate()
                    .rotation(0f)
                    .setDuration(300)
                    .start()
            }
            swipedItems.remove(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailCommentHolder {
        val binding = ListItemCocktailCommentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CocktailCommentHolder(binding)
    }

    override fun getItemCount(): Int = commentList.size

    override fun onBindViewHolder(holder: CocktailCommentHolder, position: Int) {
        holder.bindInfo(commentList[position])
    }
}