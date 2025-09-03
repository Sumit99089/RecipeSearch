package com.example.recipiesearch.presentation.home

import com.example.recipiesearch.domain.model.Recipie

data class HomeScreenState(
    val isLoading: Boolean = false,
    val isPopularRecipesLoading: Boolean = false,
    val recipes: List<Recipie> = emptyList(),
    val popularRecipes: List<Recipie> = emptyList(),
    val error: String = "",
    val searchQuery: String = ""
)