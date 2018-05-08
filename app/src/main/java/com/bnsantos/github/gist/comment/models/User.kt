package com.bnsantos.github.gist.comment.models

import com.google.gson.annotations.SerializedName

data class User(
        val id: String,
        val login: String,
        @SerializedName("avatar_url")
        val avatar: String,
        @SerializedName("html_url")
        val url: String
)