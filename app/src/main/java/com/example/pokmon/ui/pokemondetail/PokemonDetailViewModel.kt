package com.example.pokmon.ui.pokemondetail

import androidx.lifecycle.ViewModel
import com.example.pokmon.data.remote.response.Pokemon
import com.example.pokmon.repository.PokemonRepository
import com.example.pokmon.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    suspend fun getPokemonInfo(pokeName: String): Resource<Pokemon> {
        return repository.getPokemonInfo(pokeName)
    }
}