package com.example.recipiesearch.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Favourite : Screen("favourite_screen")
}
