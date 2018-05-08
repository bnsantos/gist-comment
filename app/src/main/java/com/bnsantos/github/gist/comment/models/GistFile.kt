package com.bnsantos.github.gist.comment.models

import com.google.gson.annotations.SerializedName

data class GistFile(
        val filename: String,
        val type: String,
        @SerializedName("raw_url") val url: String,
        val language: String
)