package com.example.recipiesearch.presentation.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipiesearch.domain.repository.RecipieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val repository: RecipieRepository
): ViewModel() {
    init {
        viewModelScope.launch {

        }
    }
}