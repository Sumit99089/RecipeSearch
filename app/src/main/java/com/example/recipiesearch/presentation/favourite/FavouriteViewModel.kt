package com.example.recipiesearch.presentation.favourite

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipiesearch.domain.repository.RecipieRepository
import com.example.recipiesearch.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.recipiesearch.domain.model.Recipie

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val repository: RecipieRepository
): ViewModel() {

    var state by mutableStateOf(FavouriteScreenState())
        private set

    init {
        loadFavoriteRecipes()
    }

    fun onEvent(event: FavouriteScreenEvent) {
        when(event) {
            is FavouriteScreenEvent.RemoveFromFavorites -> {
                removeFromFavorites(event.recipe)
            }
        }
    }

    private fun loadFavoriteRecipes() {
        viewModelScope.launch {
            repository
                .getFavouriteRecipies()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { recipes ->
                                state = state.copy(
                                    favoriteRecipes = recipes,
                                    error = ""
                                )
                            }
                            Log.d("FavoriteScreen", "Favorite recipes loaded: ${state.favoriteRecipes.size}")
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                error = result.message ?: "Unknown error occurred",
                                isLoading = false
                            )
                            Log.e("FavoriteScreen", "Error loading favorite recipes: ${result.message}")
                        }

                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }

    private fun removeFromFavorites(recipe: Recipie) {
        viewModelScope.launch {
            try {
                repository.clearFavouriteRecipie(recipe.id)

                // Update local state immediately for better UX
                val updatedFavorites = state.favoriteRecipes.filter { it.id != recipe.id }
                state = state.copy(favoriteRecipes = updatedFavorites)

                Log.d("FavoriteScreen", "Removed recipe from favorites: ${recipe.title}")
            } catch (e: Exception) {
                Log.e("FavoriteScreen", "Error removing from favorites: ${e.message}")
                state = state.copy(error = "Failed to remove from favorites")
            }
        }
    }
}