package com.bnsantos.github.gist.comment.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class Comment(
        val id: String,
        val user: User,
        @SerializedName("created_at") val createdAt: Date,
        @SerializedName("updated_at") val updatedAt: Date,
        val body: String
)