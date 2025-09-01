package com.example.recipiesearch.domain.model


data class Recipie(
    val id: Int,
    val image: String,
    val title: String,
    val readyInMinutes: Int,
    val isFavourite: Boolean = false
)
