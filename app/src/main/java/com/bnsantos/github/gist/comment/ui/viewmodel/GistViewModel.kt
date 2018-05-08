package com.bnsantos.github.gist.comment.ui.viewmodel

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
                    Data.Success(gist, comments)
                })
                .onErrorReturn {
                    it.printStackTrace()
                    Data.Error("Load gist error")
                }
    }
}