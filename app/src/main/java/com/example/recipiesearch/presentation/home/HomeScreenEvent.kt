package com.example.recipiesearch.presentation.home

import com.example.recipiesearch.domain.model.Recipie


sealed class HomeScreenEvent {
    data class OnSearchQuery(val query: String) : HomeScreenEvent()
    data class ToggleFavorite(val recipe: Recipie) : HomeScreenEvent()
    object Refresh: HomeScreenEvent()
}