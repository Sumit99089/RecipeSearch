package com.example.recipiesearch.presentation.home

import com.example.recipiesearch.domain.model.Recipie


sealed class HomeScreenEvent {
    object Refresh : HomeScreenEvent()
    object LoadPopularRecipes : HomeScreenEvent()
    data class OnSearchQuery(val query: String) : HomeScreenEvent()
    data class ToggleFavorite(val recipe: Recipie) : HomeScreenEvent()
    object ClearSearch : HomeScreenEvent()
}