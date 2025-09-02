package com.example.recipiesearch.presentation.favourite

import com.example.recipiesearch.domain.model.Recipie

data class FavouriteScreenState(
    val isLoading: Boolean = false,
    val favouriteRecipies: List<Recipie> = emptyList(),
    val error: String = ""
)