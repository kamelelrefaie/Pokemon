package com.example.pokmon.repository

import com.example.pokmon.data.remote.PokeApi
import com.example.pokmon.data.remote.response.Pokemon
import com.example.pokmon.data.remote.response.PokemonList
import com.example.pokmon.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import java.lang.Exception
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokeApi
) {
    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try {
            api.getPokeList(limit, offset)
        } catch (e: Exception) {
            return Resource.Error("error$e")
        }
        return Resource.Success(response)
    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try {
            api.getPokemonInfo(pokemonName)
        } catch (e: Exception) {
            return Resource.Error("error $e")
        }
        return Resource.Success(response)
    }


}