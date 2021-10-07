package com.example.pokmon.pokemonlist


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import com.example.pokmon.navigation.Screen
import com.example.pokmon.ui.theme.RobotoCondensed
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalCoilApi
@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = hiltViewModel()
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

            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), hint = "search..."
            ) {
                viewModel.searchPokemonList(it)
            }
            Spacer(modifier = Modifier.height(16.dp))
            //Create pokeList Composable
            PokemonList(navController = navController)
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier,
    hint: String,
    onSearch: (String) -> Unit
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }
    Box(modifier = modifier) {
        BasicTextField(value = text,
            onValueChange = {
                text = it
                onSearch(text)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(vertical = 12.dp, horizontal = 20.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused
                }

        )
        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp)
            )
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
                }
                loadState.append is
                        LoadState.Error -> {
                }
            }
        }
    }

}

@Composable
fun LoadingItem() {
    CircularProgressIndicator(
        modifier =
        Modifier
            .testTag("ProgressBarItem")
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(
                Alignment.CenterHorizontally
            )
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
