package com.example.recipiesearch.presentation.favourite

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipiesearch.domain.repository.RecipieRepository
import com.example.recipiesearch.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val repository: RecipieRepository
) : ViewModel() {

    var state by mutableStateOf(FavouriteScreenState())
        private set

    init {
        getFavouriteRecipies()
    }

    fun onEvent(event: FavouriteScreenEvent) {
        when (event) {
            is FavouriteScreenEvent.Refresh -> {
                getFavouriteRecipies()
            }
            is FavouriteScreenEvent.RemoveFromFavourites -> {
                removeFromFavourites(event.recipieId)
            }
        }
    }

    private fun getFavouriteRecipies() {
        viewModelScope.launch {
            repository.getFavouriteRecipies().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { recipies ->
                            state = state.copy(
                                favouriteRecipies = recipies,
                                error = ""
                            )
                        }
                    }
                    is Resource.Error -> {
                        state = state.copy(
                            error = result.message ?: "An unexpected error occurred"
                        )
                    }
                    is Resource.Loading -> {
                        state = state.copy(
                            isLoading = result.isLoading
                        )
                    }
                }
            }
        }
    }

    private fun removeFromFavourites(recipieId: Int) {
        viewModelScope.launch {
            try {
                repository.clearFavouriteRecipie(recipieId)
                // Refresh the list after removing
                getFavouriteRecipies()
            } catch (e: Exception) {
                state = state.copy(
                    error = "Failed to remove from favourites: ${e.localizedMessage}"
                )
            }
        }
    }
}
