package com.example.pokmon.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.pokmon.SplashScreen
import com.example.pokmon.pokemondetail.PokemonDetailScreen
import com.example.pokmon.pokemonlist.PokemonListScreen
import java.util.*

@ExperimentalFoundationApi
@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {

        composable(route = Screen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(route = Screen.PokemonListScreen.route) {
            PokemonListScreen(navController = navController)
        }
        composable(
            route = Screen.PokemonDetailScreen.route + "/{dominantColor}/{pokemonName}",
            arguments = listOf(navArgument("dominantColor") {
                type = NavType.IntType
            }, navArgument("pokemonName") {
                type = NavType.StringType
            })
        ) {
            val dominantColor = remember {
                val color = it.arguments?.getInt("dominantColor")
                color?.let { Color(it) } ?: Color.White
            }
            val pokemonName = remember {
                it.arguments?.getString("pokemonName")
            }
            PokemonDetailScreen(
                dominantColor = dominantColor,
                pokemonName = pokemonName?.lowercase(Locale.ROOT) ?: "",
                navController = navController
            )
        }
    }
}