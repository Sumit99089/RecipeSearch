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
            // Load popular recipes on startup
            getPopularRecipies()
            // Load all recipes with empty query (gets random recipes)
            getAllRecipies("")
        }
    }

    fun onEvent(event: HomeScreenEvent) {
        when(event) {
            is HomeScreenEvent.Refresh -> {
                getPopularRecipies()
                getAllRecipies(state.searchQuery)
            }

            is HomeScreenEvent.LoadPopularRecipes -> {
                getPopularRecipies()
            }

            is HomeScreenEvent.OnSearchQuery -> {
                state = state.copy(searchQuery = event.query, isSearching = event.query.isNotBlank())
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L) // Debounce search
                    getAllRecipies(event.query)
                }
            }

            is HomeScreenEvent.ToggleFavorite -> {
                toggleFavorite(event.recipe)
            }

            is HomeScreenEvent.ClearSearch -> {
                state = state.copy(searchQuery = "", isSearching = false)
                getAllRecipies("")
            }
        }
    }

    private fun getAllRecipies(query: String = "") {
        viewModelScope.launch {
            val searchQuery = query.trim().lowercase()

            repository
                .getAllRecipies(searchQuery.ifEmpty { "chicken" }) // Default to chicken if empty
                .collect { result ->
                    when(result) {
                        is Resource.Success -> {
                            result.data?.let { recipies ->
                                state = state.copy(
                                    recipes = recipies,
                                    error = ""
                                )
                            }
                            Log.d("HomeScreen", "Recipes loaded: ${state.recipes.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                error = result.message ?: "Unknown error occurred",
                                isLoading = false
                            )
                            Log.e("HomeScreen", "Error loading recipes: ${result.message}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
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
                                    popularRecipes = recipies,
                                    error = ""
                                )
                            }
                            Log.d("HomeScreen", "Popular recipes loaded: ${state.popularRecipes.size}")
                        }
                        is Resource.Error -> {
                            state = state.copy(
                                error = result.message ?: "Unknown error occurred",
                                isPopularRecipesLoading = false
                            )
                            Log.e("HomeScreen", "Error loading popular recipes: ${result.message}")
                        }
                        is Resource.Loading -> {
                            state = state.copy(isPopularRecipesLoading = result.isLoading)
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

                // Update local state immediately for better UX
                val updatedRecipies = state.recipes.map {
                    if (it.id == recipe.id) it.copy(isFavourite = !it.isFavourite) else it
                }
                val updatedPopularRecipies = state.popularRecipes.map {
                    if (it.id == recipe.id) it.copy(isFavourite = !it.isFavourite) else it
                }

                state = state.copy(
                    recipes = updatedRecipies,
                    popularRecipes = updatedPopularRecipies
                )

                Log.d("HomeScreen", "Toggled favorite for recipe: ${recipe.title}")
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error toggling favorite: ${e.message}")
                state = state.copy(error = "Failed to update favorite")
            }
        }
    }
}