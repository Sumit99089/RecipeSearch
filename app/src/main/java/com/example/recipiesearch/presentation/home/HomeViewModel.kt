package com.example.recipiesearch.presentation.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            repository.getAllRecipies("")
            repository.getPopularRecipies()
        }
    }

    fun onEvent(event: HomeScreenEvent) {
        when(event) {
            is HomeScreenEvent.Refresh -> {
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
                                    recipies = recipies
                                )
                            }
                        }
                        is Resource.Error -> Unit
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
                                    recipies = recipies
                                )
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }
}