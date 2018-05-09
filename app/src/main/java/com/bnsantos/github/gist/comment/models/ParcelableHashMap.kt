package com.bnsantos.github.gist.comment.models

import android.os.Parcel
import android.os.Parcelable


class ParcelableHashMap() : HashMap<String, GistFile>(), Parcelable {
    constructor(parcel: Parcel) : this() {
        val hashSize = parcel.readInt()
        for (i in 0 until hashSize) {
            val key = parcel.readString()
            val value = parcel.readParcelable<GistFile>(GistFile::class.java.classLoader)
            put(key, value)
        }
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(size)
        for (entry in entries) {
            dest?.writeString(entry.key)
            dest?.writeParcelable(entry.value, flags)
        }
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<ParcelableHashMap> {
        override fun createFromParcel(parcel: Parcel): ParcelableHashMap {
            return ParcelableHashMap(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableHashMap?> {
            return arrayOfNulls(size)
        }
    }
}