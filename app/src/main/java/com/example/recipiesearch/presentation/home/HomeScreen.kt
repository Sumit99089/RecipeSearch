package com.example.recipiesearch.presentation.home

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
     homeViewModel: HomeViewModel = hiltViewModel()
) {
    Log.d(
        "HomeScreen",
        "HomeScreen: ${homeViewModel.state.recipies}"
    )
}