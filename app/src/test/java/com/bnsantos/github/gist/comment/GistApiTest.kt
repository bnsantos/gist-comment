package com.bnsantos.github.gist.comment

import com.bnsantos.github.gist.comment.api.CommentBody
import com.bnsantos.github.gist.comment.api.GistApi
import com.bnsantos.github.gist.comment.models.Comment
import com.bnsantos.github.gist.comment.models.Gist
import com.google.gson.reflect.TypeToken
import io.reactivex.observers.TestObserver
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import java.io.IOException
import java.nio.charset.Charset

@Suppress("MemberVisibilityCanBePrivate")
class GistApiTest {
    @get:Rule
    val server = MockWebServer()
    private lateinit var api: GistApi

    @Before
    fun init() {
        api = DependencyInjector.initGistApi(server.url("/"))
    }

    @Test
    fun readGist() {
        enqueueResponse("gist.json")
        val observer = TestObserver<Gist>()
        api.read("gistId").subscribe(observer)

        val data = loadFromResource<Gist>(javaClass.classLoader, DependencyInjector.gson, "gist.json", Gist::class.java)

        observer.await()
                .assertNoErrors()
                .assertValue(data)
                .assertComplete()
    }

    @Test
    fun gistNotFound() {
        server.enqueue(MockResponse().setResponseCode(404).setBody("{\"message\":\"Not Found\",\"documentation_url\":\"https://developer.github.com/v3/gists/#get-a-single-gist\"}"))
        val observer = TestObserver<Gist>()
        api.read("gistId").subscribe(observer)

        observer.await()
                .assertError({
                    it is HttpException && it.code() == 404
                })
    }

    @Test
    fun readComments() {
        enqueueResponse("read_comments.json")
        val observer = TestObserver<List<Comment>>()
        api.comments("gistId").subscribe(observer)

        val data = loadFromResource<List<Comment>>(javaClass.classLoader, DependencyInjector.gson, "read_comments.json", object : TypeToken<List<Comment>>() {
        }.type)

        observer.await()
                .assertNoErrors()
                .assertValue(data)
                .assertComplete()
    }

    @Test
    fun commentsNotFound() {
        server.enqueue(MockResponse().setResponseCode(404).setBody("{\"message\":\"Not Found\",\"documentation_url\":\"https://developer.github.com/v3/gists/comments/#list-comments-on-a-gist\"}"))
        val observer = TestObserver<List<Comment>>()
        api.comments("gistId").subscribe(observer)

        observer.await()
                .assertError({
                    it is HttpException && it.code() == 404
                })
    }

    @Test
    fun createComment() {
        enqueueResponse("create_comment.json")
        val observer = TestObserver<Comment>()
        api.comment("gistId", CommentBody("body")).subscribe(observer)

        val data = loadFromResource<Comment>(javaClass.classLoader, DependencyInjector.gson, "create_comment.json", Comment::class.java)

        observer.await()
                .assertNoErrors()
                .assertValue(data)
                .assertComplete()
    }

    @Test
    fun editComment() {
        enqueueResponse("edit_comment.json")
        val observer = TestObserver<Comment>()
        api.editComment("gistId", "commentId", CommentBody("body")).subscribe(observer)

        val data = loadFromResource<Comment>(javaClass.classLoader, DependencyInjector.gson, "edit_comment.json", Comment::class.java)

        observer.await()
                .assertNoErrors()
                .assertValue(data)
                .assertComplete()
    }

    @Test
    fun createEditCommentUnauthorized() {
        server.enqueue(MockResponse().setResponseCode(404).setBody("{\"message\":\"Not Found\",\"documentation_url\":\"https://developer.github.com/v3/gists/comments/#create-a-comment\"}"))
        val observer = TestObserver<Comment>()
        api.editComment("gistId", "commentId", CommentBody("body")).subscribe(observer)

        observer.await()
                .assertError({
                    it is HttpException && it.code() == 404
                })
    }

    @Throws(IOException::class)
    private fun enqueueResponse(fileName: String) {
        val inputStream = javaClass.classLoader.getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        server.enqueue(mockResponse.setBody(source.readString(Charset.forName("UTF-8"))))
    }
}