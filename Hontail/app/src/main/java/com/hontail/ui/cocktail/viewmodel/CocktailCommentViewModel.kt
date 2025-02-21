package com.hontail.ui.cocktail.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hontail.data.model.response.Comment
import com.hontail.data.remote.RetrofitUtil
import kotlinx.coroutines.launch
import retrofit2.Response

private const val TAG = "CocktailCommentViewMode"
class CocktailCommentViewModel: ViewModel() {

    private val commentService = RetrofitUtil.commentService

    // 칵테일 Id
    private val _cocktailId = MutableLiveData<Int>()
    val cocktailId: LiveData<Int> get() = _cocktailId

    // User Id
    private val _userId = MutableLiveData<Int>()
    val userId: LiveData<Int> get() = _userId

    // 댓글 리스트
    private val _comments = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>> get() = _comments


    fun setCocktailId(cocktailId: Int) {
        _cocktailId.value = cocktailId
        loadComments()
    }

    fun setUserId(userId: Int) {
        _userId.value = userId ?: 0
    }

    // 댓글 작성
    fun insertComment(content: String) {
        val cocktailId = _cocktailId.value ?: return

        viewModelScope.launch {
            try {
                val response: Response<Comment> = commentService.insertComment(cocktailId, content)
                if (response.isSuccessful) {
                    Log.d(TAG, "insertComment: 댓글 작성 성공")
                    loadComments() // 댓글 목록 다시 불러오기
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.d(TAG, "insertComment: 댓글 작성 실패 : $errorMsg")
                }
            } catch (e: Exception) {
                Log.d(TAG, "insertComment: 오류 발생 : ${e.message}")
            }
        }
    }

    // 댓글 수정
    fun updateComment(commentId: Int, newContent: String) {
        val cocktailId = _cocktailId.value ?: return

        viewModelScope.launch {
            try {
                val response = commentService.updateComment(cocktailId, commentId, newContent)
                if (response.isSuccessful) {
                    Log.d(TAG, "updateComment: 댓글 수정 성공")
                    loadComments()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.d(TAG, "updateComment: 댓글 수정 실패 : $errorMsg")
                }
            } catch (e: Exception) {
                Log.d(TAG, "updateComment: 오류 발생 : ${e.message}")
            }
        }
    }

    // 댓글 삭제
    fun deleteComment(commentId: Int) {
        val cocktailId = _cocktailId.value ?: return

        viewModelScope.launch {
            try {
                val response = commentService.deleteComment(cocktailId, commentId)
                if (response.isSuccessful) {
                    Log.d(TAG, "deleteComment: 댓글 삭제 성공")
                    loadComments()
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.d(TAG, "deleteComment: 댓글 삭제 실패 : $errorMsg")
                }
            } catch (e: Exception) {
                Log.d(TAG, "deleteComment: 오류 발생 : ${e.message}")
            }
        }
    }

    // 댓글 목록 조회
    fun loadComments() {
        val cocktailId = _cocktailId.value ?: return

        viewModelScope.launch {
            try {
                val response: Response<List<Comment>> = commentService.getComments(cocktailId)
                if (response.isSuccessful) {
                    Log.d(TAG, "loadComments: 댓글 조회 성공")
                    _comments.postValue(response.body())
                } else {
                    val errorMsg = response.errorBody()?.string()
                    Log.d(TAG, "loadComment: 댓글 조회 실패 : $errorMsg")
                }
            } catch (e: Exception) {
                Log.d(TAG, "loadComments: ${e.message}")
            }
        }
    }
}