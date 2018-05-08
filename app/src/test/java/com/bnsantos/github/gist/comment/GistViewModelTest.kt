package com.bnsantos.github.gist.comment

import com.bnsantos.github.gist.comment.api.GistApi
import com.bnsantos.github.gist.comment.models.Comment
import com.bnsantos.github.gist.comment.models.Gist
import com.bnsantos.github.gist.comment.ui.viewmodel.Data
import com.bnsantos.github.gist.comment.ui.viewmodel.GistViewModel
import com.google.gson.reflect.TypeToken
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito

class GistViewModelTest {
    private val api: GistApi = Mockito.mock(GistApi::class.java)
    private lateinit var viewModel: GistViewModel

    @Before
    fun init() {
        viewModel = GistViewModel(api)
    }

    @Test
    fun loadOk() {
        val gist = loadFromResource<Gist>(javaClass.classLoader, DependencyInjector.gson, "gist.json", Gist::class.java)
        val comments = loadFromResource<List<Comment>>(javaClass.classLoader, DependencyInjector.gson, "read_comments.json", object : TypeToken<List<Comment>>() {
        }.type)

        Mockito.`when`(api.read(anyString())).thenReturn(Observable.just(gist))
        Mockito.`when`(api.comments(anyString())).thenReturn(Observable.just(comments))

        val observer = TestObserver<Data>()

        viewModel.gistId = "gistId"
        viewModel.load().subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.Success(gist, comments))
    }

    @Test
    fun loadGistError() {
        val comments = loadFromResource<List<Comment>>(javaClass.classLoader, DependencyInjector.gson, "read_comments.json", object : TypeToken<List<Comment>>() {
        }.type)

        Mockito.`when`(api.read(anyString())).thenReturn(Observable.error(Exception("test exception")))
        Mockito.`when`(api.comments(anyString())).thenReturn(Observable.just(comments))

        val observer = TestObserver<Data>()

        viewModel.gistId = "gistId"
        viewModel.load().subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.Error("Load gist error"))
    }

    @Test
    fun loadCommentsError() {
        val gist = loadFromResource<Gist>(javaClass.classLoader, DependencyInjector.gson, "gist.json", Gist::class.java)

        Mockito.`when`(api.read(anyString())).thenReturn(Observable.error(Exception("test exception")))
        Mockito.`when`(api.comments(anyString())).thenReturn(Observable.error(Exception("test exception")))

        val observer = TestObserver<Data>()

        viewModel.gistId = "gistId"
        viewModel.load().subscribe(observer)

        observer.await()
                .assertNoErrors()
                .assertValue(Data.Error("Load gist error"))
    }

}