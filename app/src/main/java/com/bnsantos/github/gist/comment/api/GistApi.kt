package com.bnsantos.github.gist.comment.api

import com.bnsantos.github.gist.comment.models.Comment
import com.bnsantos.github.gist.comment.models.Gist
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface GistApi {
    @GET("{id}")
    fun read(@Path("id") id: String): Observable<Gist>

    @GET("{id}/comments")
    fun comments(@Path("id") id: String): Observable<List<Comment>>

    @POST("{id}/comments")
    fun comment(@Path("id") id: String, @Body body: CommentBody): Observable<Comment>

    @PATCH("{gist}/comments/{comment}")
    fun editComment(@Path("gist") gist: String, @Path("comment") comment: String, @Body body: CommentBody): Observable<Comment>

    @DELETE("{gist}/comments/{comment}")
    fun deleteComment(@Path("gist") gist: String, @Path("comment") comment: String): Observable<Response<Unit>>
}