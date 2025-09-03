package com.example.recipiesearch.presentation.favourite

import com.example.recipiesearch.domain.model.Recipie

sealed class FavouriteScreenEvent {
    data class RemoveFromFavorites(val recipe: Recipie) : FavouriteScreenEvent()
}