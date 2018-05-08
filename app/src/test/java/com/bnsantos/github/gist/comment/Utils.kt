package com.bnsantos.github.gist.comment

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.reflect.Type

@Throws(IOException::class)
fun <T> loadFromResource(loader: ClassLoader, gson: Gson, fileName: String, type: Type): T {
    val inputStream = loader.getResourceAsStream("api-response/$fileName")
    val inputStreamReader = InputStreamReader(inputStream)
    val reader = JsonReader(inputStreamReader)
    val data = gson.fromJson<T>(reader, type)
    reader.close()
    return data
}