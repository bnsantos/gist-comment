package com.bnsantos.github.gist.comment.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
        val id: String,
        val login: String,
        @SerializedName("avatar_url")
        val avatar: String,
        @SerializedName("html_url")
        val url: String
) : Parcelable