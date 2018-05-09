package com.bnsantos.github.gist.comment.ui.viewmodel

import com.bnsantos.github.gist.comment.models.Comment
import com.bnsantos.github.gist.comment.models.Gist

sealed class Data {
    data class SuccessLoading(val gist: Gist, val comments: List<Comment>) : Data()
    data class SuccessComment(val comment: Comment) : Data()
    data class SuccessDeleteComment(val status: Int) : Data()

    data class ErrorLoading(val message: String) : Data()
    data class ErrorComment(val message: String) : Data()
    data class ErrorDeleteComment(val message: String) : Data()
}