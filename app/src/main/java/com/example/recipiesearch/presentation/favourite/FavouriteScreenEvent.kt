package com.example.recipiesearch.presentation.favourite

import com.example.recipiesearch.domain.model.Recipie

sealed class FavouriteScreenEvent {
    object LoadFavorites : FavouriteScreenEvent()
    object Refresh : FavouriteScreenEvent()
    data class RemoveFromFavorites(val recipe: Recipie) : FavouriteScreenEvent()
    data class ToggleFavorite(val recipe: Recipie) : FavouriteScreenEvent()
}