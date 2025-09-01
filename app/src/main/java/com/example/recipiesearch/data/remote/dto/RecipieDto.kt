package com.example.recipiesearch.data.remote.dto

import com.squareup.moshi.Json

data class RecipieDto(
    val id: Int,
    val image: String,
    val title: String,
    val readyInMinutes: Int = 0,
)
