package com.bnsantos.github.gist.comment.ui.viewmodel

import com.bnsantos.github.gist.comment.api.CommentBody
import com.bnsantos.github.gist.comment.api.GistApi
import com.bnsantos.github.gist.comment.models.Comment
import com.bnsantos.github.gist.comment.models.Gist
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class GistViewModel(private val api: GistApi) {
    lateinit var gistId: String

    fun load(): Observable<Data> {
        return api.read(gistId)
                .zipWith(api.comments(gistId), BiFunction<Gist, List<Comment>, Data> { gist, comments ->
                    Data.SuccessLoading(gist, comments)
                })
                .onErrorReturn {
                    it.printStackTrace()
                    Data.ErrorLoading("Load gist error")
                }
    }

    fun createComment(body: String): Observable<Data> {
        return api.comment(gistId, CommentBody(body))
                .map {
                    Data.SuccessComment(it) as Data
                }
                .onErrorReturn {
                    Data.ErrorComment("Error while creating comment")
                }
    }

    fun deleteComment(commentId: String): Observable<Data> {
        return api.deleteComment(gistId, commentId)
                .map {
                    if (it.code() == 204) {
                        Data.SuccessDeleteComment(it.code())
                    } else {
                        Data.ErrorDeleteComment("Error while deleting comment $commentId")
                    }
                }
                .onErrorReturn {
                    Data.ErrorDeleteComment("Error while deleting comment $commentId")
                }
    }

    fun editComment(commentId: String, newBody: String): Observable<Data> {
        return api.editComment(gistId, commentId, CommentBody(newBody))
                .map {
                    Data.SuccessComment(it) as Data
                }
                .onErrorReturn {
                    Data.ErrorComment("Error while creating comment")
                }
    }
}