package com.example.recipiesearch.presentation.favourite

sealed class FavouriteScreenEvent {
    object Refresh : FavouriteScreenEvent()
    data class RemoveFromFavourites(val recipieId: Int) : FavouriteScreenEvent()
}