package com.example.eltiempo.data


import com.squareup.moshi.Json

data class Clouds(
    @Json(name = "all")
    val all: Int?
)