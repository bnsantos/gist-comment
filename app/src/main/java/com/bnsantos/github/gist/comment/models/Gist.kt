package com.bnsantos.github.gist.comment.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Gist(
        val id: String,
        val url: String,
        val description: String,
        @SerializedName("created_at") val createdAt: Date,
        @SerializedName("updated_at") val updatedAt: Date,
        val comments: Int,
        val owner: User
)