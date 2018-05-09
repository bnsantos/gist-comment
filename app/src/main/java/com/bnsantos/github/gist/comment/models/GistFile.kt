package com.bnsantos.github.gist.comment.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GistFile(
        val filename: String,
        val type: String,
        @SerializedName("raw_url") val url: String,
        val language: String
) : Parcelable