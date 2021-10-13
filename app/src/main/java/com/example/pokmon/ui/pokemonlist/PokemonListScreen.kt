package com.example.pokmon.ui.pokemonlist


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.pokmon.R
import com.example.pokmon.data.models.PokeListEntry
import com.example.pokmon.ui.navigation.Screen
import com.example.pokmon.ui.theme.RobotoCondensed
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalCoilApi
@Composable
fun PokemonListScreen(
    navController: NavController,
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            //poke image
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "logo-pokemon",
                modifier = Modifier.fillMaxWidth(),
                alignment = Center
            )

            Spacer(modifier = Modifier.height(10.dp))
            //Create pokeList Composable
            PokemonList(navController = navController)
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalCoilApi
@Composable
fun PokemonList(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel(),
) {

    val lazyPokeList = viewModel.pokeList
    val pokeListItems: LazyPagingItems<PokeListEntry> = lazyPokeList.collectAsLazyPagingItems()
    LazyVerticalGrid(cells = GridCells.Fixed(2)) {
        items(pokeListItems.itemCount) { index ->
            pokeListItems[index]?.let {
                PokeEntry(entry = it, navController = navController)
            }
        }

        pokeListItems.apply {
            when {
                loadState.refresh is
                        LoadState.Loading -> {
                    item { LoadingItem() }
                    item { LoadingItem() }

                }
                loadState.append is
                        LoadState.Loading -> {
                    item { LoadingItem() }
                    item { LoadingItem() }
                }
                loadState.refresh is
                        LoadState.Error -> {
                    item { ErrorItem() }
                }
                loadState.append is
                        LoadState.Error -> {
                    item { ErrorItem() }
                }
            }
        }
    }

}

@Composable
fun LoadingItem() {
    CircularProgressIndicator(
        color = Color.White,
        modifier =
        Modifier
            .testTag("ProgressBarItem")
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(
                CenterHorizontally
            )
    )

}

@Composable
fun ErrorItem() {
    Text(
        text = "Check your internet Connection",
        color = Color.Red, textAlign = TextAlign.Center
    )
}


@ExperimentalCoilApi
@Composable
fun PokeEntry(
    entry: PokeListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = hiltViewModel()
) {
    val defaultDominantColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    Box(
        modifier = modifier
            .padding(16.dp)
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .clickable {
                //"pokemon_detailed_screen/${dominantColor.toArgb()}/${entry.pokeName}"
                navController.navigate(
                    Screen.PokemonDetailScreen.withArgs(
                        "${dominantColor.toArgb()}",
                        entry.pokeName
                    )
                )
            },
        contentAlignment = Center
    ) {
        Column {
            val context = LocalContext.current

            val imageLoader = ImageLoader(context)

            val request = ImageRequest.Builder(context)
                .crossfade(true)
                .data(entry.imageUrl)
                .build()

            val imagePainter = rememberImagePainter(
                request = request,
                imageLoader = imageLoader
            )

            LaunchedEffect(key1 = imagePainter) {
                launch {
                    val result = (imageLoader.execute(request) as SuccessResult).drawable
                    viewModel.calcDominantColor(result) {
                        dominantColor = it
                    }
                }
            }

            Image(
                painter = imagePainter,
                contentDescription = entry.pokeName,
                modifier = Modifier
                    .size(128.dp)
                    .align(CenterHorizontally)
            )
            Text(
                text = entry.pokeName,
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
