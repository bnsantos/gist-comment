package com.bnsantos.github.gist.comment.ui.activity

import com.bnsantos.github.gist.comment.models.Comment

interface CommentListener {
    fun openLink(link: String)
    fun onLongClick(pos: Int, comment: Comment): Boolean
}