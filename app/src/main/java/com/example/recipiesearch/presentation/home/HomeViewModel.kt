package com.example.recipiesearch.presentation.home


import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipiesearch.domain.model.Recipie
import com.example.recipiesearch.domain.repository.RecipieRepository
import com.example.recipiesearch.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecipieRepository
): ViewModel() {
    var state by mutableStateOf(HomeScreenState())
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            getPopularRecipies() // Load popular recipes for the horizontal row
            getAllRecipies() // Load all recipes for the main column
        }
    }

    fun onEvent(event: HomeScreenEvent) {
        when(event) {
            HomeScreenEvent.Refresh -> {
                getPopularRecipies()
                getAllRecipies()
            }
            is HomeScreenEvent.OnSearchQuery -> {
                state = state.copy(searchQuery = event.query)
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getAllRecipies()
                }
            }
            HomeScreenEvent.ClearSearch -> {
                state = state.copy(searchQuery = "")
                searchJob?.cancel()
                getAllRecipies()
            }
            HomeScreenEvent.LoadPopularRecipes -> {
                getPopularRecipies()
            }
            is HomeScreenEvent.ToggleFavorite -> {
                toggleFavorite(event.recipe)
            }
        }
    }

    private fun getAllRecipies(
        query: String = state.searchQuery.lowercase()
    ) {
        viewModelScope.launch {
            repository
                .getAllRecipies(query)
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { recipies ->
                                state = state.copy(
                                    recipies = recipies,
                                    error = ""
                                )
                            }
                            Log.d(
                                "HomeScreen",
                                "HomeScreen: ${state.recipies}"
                            )
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                error = result.message ?: "An unexpected error occurred"
                            )
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }

    private fun toggleFavorite(recipe: Recipie) {
        viewModelScope.launch {
            try {
                if (recipe.isFavourite) {
                    repository.clearFavouriteRecipie(recipe.id)
                } else {
                    repository.insertFavouriteRecipie(recipe)
                }

                // Refresh the current list to update favorite status
                if (state.searchQuery.isEmpty()) {
                    getPopularRecipies()
                } else {
                    getAllRecipies()
                }
            } catch (e: Exception) {
                state = state.copy(
                    error = "Failed to update favorite: ${e.localizedMessage}"
                )
            }
        }
    }

    private fun getPopularRecipies() {
        viewModelScope.launch {
            repository
                .getPopularRecipies()
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { recipies ->
                                state = state.copy(
                                    popularRecipies = recipies,
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
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}