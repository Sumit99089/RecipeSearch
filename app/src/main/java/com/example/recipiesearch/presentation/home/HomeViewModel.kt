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
                refreshAllData()
            }

            is HomeScreenEvent.LoadPopularRecipes -> {
                getPopularRecipies()
            }

            is HomeScreenEvent.RefreshFavoriteStates -> {
                refreshFavoriteStates()
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

    private fun refreshAllData() {
        viewModelScope.launch {
            // Start loading state
            state = state.copy(isLoading = true, isPopularRecipesLoading = true)

            // Refresh popular recipes
            getPopularRecipies()

            // Refresh current recipe list
            getAllRecipies(state.searchQuery.ifEmpty { "" })

            // Refresh favorite states
            refreshFavoriteStates()
        }
    }

    private fun refreshFavoriteStates() {
        viewModelScope.launch {
            try {
                // Get current favorite IDs from database
                val favoriteIds = repository.getFavouriteRecipies().let { flow ->
                    var ids: List<Int> = emptyList()
                    flow.collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                ids = result.data?.map { it.id } ?: emptyList()
                            }
                            else -> {}
                        }
                    }
                    ids
                }

                // Update recipe states
                val updatedRecipes = state.recipes.map { recipe ->
                    recipe.copy(isFavourite = favoriteIds.contains(recipe.id))
                }

                val updatedPopularRecipes = state.popularRecipes.map { recipe ->
                    recipe.copy(isFavourite = favoriteIds.contains(recipe.id))
                }

                state = state.copy(
                    recipes = updatedRecipes,
                    popularRecipes = updatedPopularRecipes
                )

                Log.d("HomeScreen", "Refreshed favorite states for ${favoriteIds.size} favorites")
            } catch (e: Exception) {
                Log.e("HomeScreen", "Error refreshing favorite states: ${e.message}")
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
                                // Get current favorite IDs to maintain state
                                val favoriteIds = getFavoriteIds()

                                val recipesWithFavoriteState = recipies.map { recipe ->
                                    recipe.copy(isFavourite = favoriteIds.contains(recipe.id))
                                }

                                state = state.copy(
                                    recipes = recipesWithFavoriteState,
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
                                // Get current favorite IDs to maintain state
                                val favoriteIds = getFavoriteIds()

                                val recipesWithFavoriteState = recipies.map { recipe ->
                                    recipe.copy(isFavourite = favoriteIds.contains(recipe.id))
                                }

                                state = state.copy(
                                    popularRecipes = recipesWithFavoriteState,
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

    private suspend fun getFavoriteIds(): List<Int> {
        return try {
            var ids: List<Int> = emptyList()
            repository.getFavouriteRecipies().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        ids = result.data?.map { it.id } ?: emptyList()
                    }
                    else -> {}
                }
            }
            ids
        } catch (e: Exception) {
            Log.e("HomeScreen", "Error getting favorite IDs: ${e.message}")
            emptyList()
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