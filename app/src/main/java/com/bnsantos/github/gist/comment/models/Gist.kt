package com.bnsantos.github.gist.comment.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Gist(
        val id: String,
        @SerializedName("html_url")
        val url: String,
        val description: String,
        @SerializedName("created_at") val createdAt: Date,
        @SerializedName("updated_at") val updatedAt: Date,
        val comments: Int,
        val owner: User,
        val files: ParcelableHashMap
) : Parcelable