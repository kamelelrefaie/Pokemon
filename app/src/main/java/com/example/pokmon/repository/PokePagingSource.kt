package com.example.pokmon.repository


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.pokmon.data.models.PokeListEntry

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject

class PokePagingSource @Inject constructor(private val repository: PokemonRepository) :
    PagingSource<Int, PokeListEntry>() {


    override fun getRefreshKey(state: PagingState<Int, PokeListEntry>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PokeListEntry> {

        val nextPage = params.key ?: 1
        val result = repository.getPokemonList(20, (20 * (nextPage-1)))
        val pokedexEntries = result.data?.results?.mapIndexed { index, entry ->
            val number = if (entry.url.endsWith("/")) {
                entry.url.dropLast(1).takeLastWhile { it.isDigit() }
            } else {
                entry.url.takeLastWhile { it.isDigit() }
            }
            val url =
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
            PokeListEntry(entry.name.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.ROOT
                ) else it.toString()
            }, url, number.toInt())
        }




        return LoadResult.Page(
            data = pokedexEntries!!,
            prevKey = if (nextPage == 1) null else nextPage - 1,
            nextKey = if (pokedexEntries.isEmpty()) null else (nextPage.plus(1))
        )
    }
//            else -> {
//                return LoadResult.Error(Exception(pokeList.message) )
//            }

}




