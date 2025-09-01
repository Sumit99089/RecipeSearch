package com.example.recipiesearch.presentation.home

sealed class HomeScreenEvent{
    object Refresh: HomeScreenEvent()
    data class OnSearchQuery(val query: String): HomeScreenEvent()
}
