package com.bnsantos.github.gist.comment

import com.bnsantos.github.gist.comment.api.GistApi
import com.bnsantos.github.gist.comment.models.Comment
import com.bnsantos.github.gist.comment.models.Gist
import com.bnsantos.github.gist.comment.ui.viewmodel.Data
import com.bnsantos.github.gist.comment.ui.viewmodel.GistViewModel
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import okhttp3.Protocol
import okhttp3.Request
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import retrofit2.Response.success

class GistViewModelTest {
    private val api: GistApi = Mockito.mock(GistApi::class.java)
    private lateinit var viewModel: GistViewModel

    @Before
    fun init() {
        viewModel = GistViewModel(api)
        viewModel.gistId = "gistId"
    }

    @Test
    fun loadOk() {
        val gist = loadFromResource<Gist>(javaClass.classLoader, DependencyInjector.gson, "gist.json", Gist::class.java)
        val comments = loadFromResource<List<Comment>>(javaClass.classLoader, DependencyInjector.gson, "read_comments.json", object : TypeToken<List<Comment>>() {
        }.type)

        Mockito.`when`(api.read(anyString())).thenReturn(Observable.just(gist))
        Mockito.`when`(api.comments(anyString())).thenReturn(Observable.just(comments))

        val observer = TestObserver<Data>()

        viewModel.load().subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.SuccessLoading(gist, comments))
    }

    @Test
    fun loadGistError() {
        val comments = loadFromResource<List<Comment>>(javaClass.classLoader, DependencyInjector.gson, "read_comments.json", object : TypeToken<List<Comment>>() {
        }.type)

        Mockito.`when`(api.read(anyString())).thenReturn(Observable.error(Exception("test exception")))
        Mockito.`when`(api.comments(anyString())).thenReturn(Observable.just(comments))

        val observer = TestObserver<Data>()

        viewModel.load().subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.ErrorLoading("Load gist error"))
    }

    @Test
    fun loadCommentsError() {
        val gist = loadFromResource<Gist>(javaClass.classLoader, DependencyInjector.gson, "gist.json", Gist::class.java)
        Mockito.`when`(api.read(anyString())).thenReturn(Observable.just(gist))
        Mockito.`when`(api.comments(anyString())).thenReturn(Observable.error(Exception("test exception")))

        val observer = TestObserver<Data>()

        viewModel.load().subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.ErrorLoading("Load gist error"))
    }

    @Test
    fun loadError() {
        Mockito.`when`(api.read(anyString())).thenReturn(Observable.error(Exception("test exception")))
        Mockito.`when`(api.comments(anyString())).thenReturn(Observable.error(Exception("test exception")))

        val observer = TestObserver<Data>()

        viewModel.load().subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.ErrorLoading("Load gist error"))
    }

    @Test
    fun createComment() {
        val data = loadFromResource<Comment>(javaClass.classLoader, DependencyInjector.gson, "create_comment.json", Comment::class.java)
        Mockito.`when`(api.comment(anyString(), any())).thenReturn(Observable.just(data))

        val observer = TestObserver<Data>()
        viewModel.createComment("comment test").subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.SuccessComment(data))
    }

    @Test
    fun createCommentError() {
        Mockito.`when`(api.comment(anyString(), any())).thenReturn(Observable.error(Exception("test exception")))

        val observer = TestObserver<Data>()
        viewModel.createComment("comment test").subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.ErrorComment("Error while creating comment"))
    }

    @Test
    fun deleteComment() {
        Mockito.`when`(api.deleteComment(anyString(), anyString())).thenReturn(Observable.just(
                success<Unit>(Unit, okhttp3.Response.Builder()
                        .code(204)
                        .message("OK")
                        .protocol(Protocol.HTTP_1_1)
                        .request(Request.Builder().url("http://localhost/").build())
                        .build())))

        val observer = TestObserver<Data>()
        viewModel.deleteComment("commentId").subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.SuccessDeleteComment(204))
    }

    @Test
    fun deleteCommentError() {
        Mockito.`when`(api.deleteComment(anyString(), anyString())).thenReturn(Observable.error(Exception("test exception")))

        val observer = TestObserver<Data>()
        viewModel.deleteComment("commentId").subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.ErrorDeleteComment("Error while deleting comment commentId"))
    }

    @Test
    fun editComment() {
        val data = loadFromResource<Comment>(javaClass.classLoader, DependencyInjector.gson, "edit_comment.json", Comment::class.java)
        Mockito.`when`(api.editComment(anyString(), any(), any())).thenReturn(Observable.just(data))

        val observer = TestObserver<Data>()
        viewModel.editComment("comment test", "new body").subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.SuccessComment(data))
    }

    @Test
    fun editCommentError() {
        Mockito.`when`(api.editComment(anyString(), any(), any())).thenReturn(Observable.error(Exception("test exception")))

        val observer = TestObserver<Data>()
        viewModel.editComment("comment test", "new body").subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.ErrorComment("Error while creating comment"))
    }
}