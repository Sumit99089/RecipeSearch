package com.example.recipiesearch.presentation.home

import com.example.recipiesearch.domain.model.Recipie

data class HomeScreenState(
    val isLoading: Boolean = false,
    val recipies: List<Recipie> = emptyList(),
    val popularRecipies: List<Recipie> = emptyList(),
    val error: String = "",
    val searchQuery: String = "" // Changed from Boolean to String
)