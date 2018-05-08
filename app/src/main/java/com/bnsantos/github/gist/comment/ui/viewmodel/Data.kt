package com.bnsantos.github.gist.comment.ui.viewmodel

import com.bnsantos.github.gist.comment.models.Comment
import com.bnsantos.github.gist.comment.models.Gist

sealed class Data {
    data class Success(val gist: Gist, val comments: List<Comment>) : Data()
    data class Error(val message: String) : Data()
}