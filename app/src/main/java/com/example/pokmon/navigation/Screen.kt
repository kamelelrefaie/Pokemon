package com.example.pokmon.navigation

sealed class Screen(val route: String) {
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