package com.example.recipiesearch.data.remote.dto

data class RecipieDto(
    val id: Int,
    val image: String,
    val title: String,
    val readyInMinutes: Int = 0,
)
