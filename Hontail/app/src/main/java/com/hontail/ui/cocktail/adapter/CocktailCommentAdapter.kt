package com.hontail.ui.cocktail.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hontail.R
import com.hontail.data.model.response.Comment
import com.hontail.databinding.ListItemCocktailCommentBinding

private const val TAG = "CocktailCommentAdapter"
class CocktailCommentAdapter(private val context: Context, private var commentList: MutableList<Comment>, private val userId: Int?) : RecyclerView.Adapter<CocktailCommentAdapter.CocktailCommentHolder>() {

    lateinit var cocktailCommentListener: ItemOnClickListener

    interface ItemOnClickListener {
        fun onClickDelete(commentId: Int)
        fun onClickModify(commentId: Int, content: String)
    }

    private val swipedItems = mutableSetOf<Int>()
    private val SWIPE_AMOUNT = -320f

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CocktailCommentHolder {
        val binding = ListItemCocktailCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CocktailCommentHolder(binding)
    }

    override fun getItemCount(): Int = commentList.size

    override fun onBindViewHolder(holder: CocktailCommentHolder, position: Int) {
        holder.bindInfo(commentList[position])
    }

    fun updateComments(newComments: List<Comment>) {
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = commentList.size
            override fun getNewListSize() = newComments.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return commentList[oldItemPosition].commentId == newComments[newItemPosition].commentId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return commentList[oldItemPosition] == newComments[newItemPosition]
            }
        }

        val diffResult = DiffUtil.calculateDiff(diffCallback)

        // 🔥 새로운 리스트 적용 전에 기존 리스트에서 삭제된 아이템을 감지!
        val deletedComments = commentList.map { it.commentId } - newComments.map { it.commentId }

        // 🔥 삭제된 댓글과 관련된 스와이프 상태 초기화
        swipedItems.removeAll(deletedComments)

        commentList = newComments.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }


    inner class CocktailCommentHolder(private val binding: ListItemCocktailCommentBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindInfo(item: Comment) {
            binding.apply {
                // 수정/삭제 버튼 초기 상태 설정
                imageViewEdit.isEnabled = false
                imageViewDelete.isEnabled = false

                Glide.with(context)
                    .load(item.userImageUrl)
                    .placeholder(R.drawable.logo_image)
                    .into(imageViewCocktailCommentUserImage)

                textViewCocktailCommentUserName.text = item.userNickname
                textViewCocktailCommentUserComment.text = item.content

                Log.d(TAG, "bindInfo: 댓글 userId: ${item.userId} / 로그인한 userId: $userId / 댓글 닉네임 : ${item.userNickname} / 댓글 내용: ${item.content}")

                // 현재 로그인 한 사용자 id와 같은 댓글 사용자 id에서만 활성화되도록 '>'
                if (item.userId == userId) {
                    imageViewCocktailCommentUserComment.visibility = View.VISIBLE
                } else {
                    imageViewCocktailCommentUserComment.visibility = View.GONE
                }


                // 초기 상태 설정
                contentContainer.translationX = if (swipedItems.contains(adapterPosition)) SWIPE_AMOUNT else 0f
                imageViewCocktailCommentUserComment.rotation = if (swipedItems.contains(adapterPosition)) 180f else 0f

                // 스와이프 상태에 따른 버튼 상태 업데이트
                updateButtonStates(swipedItems.contains(adapterPosition))

                // 화살표 버튼 클릭 이벤트
                imageViewCocktailCommentUserComment.setOnClickListener {
                    if (imageViewCocktailCommentUserComment.isEnabled) {
                        if (swipedItems.contains(adapterPosition)) {
                            resetSwipe()
                        } else {
                            swipeItem()
                        }
                    }
                }

                // 수정 모드
                imageViewEdit.setOnClickListener {
                    if (imageViewEdit.isEnabled) {
                        cocktailCommentListener.onClickModify(item.commentId, item.content)
                        resetSwipe()
                    }
                }

                // 삭제
                imageViewDelete.setOnClickListener {
                    if (imageViewDelete.isEnabled) {
                        cocktailCommentListener.onClickDelete(item.commentId)
                        swipedItems.remove(item.commentId)
                    }
                }
            }
        }

        private fun updateButtonStates(isSwipedState: Boolean) {
            binding.apply {
                imageViewEdit.isEnabled = isSwipedState
                imageViewDelete.isEnabled = isSwipedState
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

                // 수정, 삭제 버튼 활성화
                updateButtonStates(true)
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

                // 수정, 삭제 버튼 비활성화
                updateButtonStates(false)
            }
            swipedItems.remove(adapterPosition)
        }
    }
}