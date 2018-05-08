package com.bnsantos.github.gist.comment

import com.bnsantos.github.gist.comment.api.GistApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


object DependencyInjector {
    fun initGistApi(httpUrl: HttpUrl): GistApi {
        val retrofit = Retrofit.Builder()
                .baseUrl(httpUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
        return retrofit.create(GistApi::class.java)
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
                .addInterceptor { chain ->
                    chain.proceed(chain.request()
                            .newBuilder()
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", "Bearer ${BuildConfig.TOKEN}")
                            .build()
                    )
                }
                .build()
    }

    val gson: Gson by lazy {
        GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ") //"2018-05-08T20:20:08Z"
                .create()
    }

    private val gistApi: GistApi by lazy {
        initGistApi(HttpUrl.parse(BuildConfig.URI)!!)
    }
}