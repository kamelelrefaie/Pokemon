package com.example.pokmon.ui.navigation

import android.annotation.SuppressLint

sealed class Screen(val route: String) {
    @SuppressLint("CustomSplashScreen")
    object SplashScreen: Screen("pokemon_splash_screen")
    object PokemonListScreen : Screen("pokemon_list_screen")
    object PokemonDetailScreen : Screen("pokemon_detailed_screen")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}