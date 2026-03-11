package com.example.weatherforecast.view.favoritesScreen.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.labs.R
import com.example.weatherforecast.data.db.WeatherDatabase
import com.example.weatherforecast.data.network.RetrofitHelper
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.dataSource.local.WeatherLocalSource
import com.example.weatherforecast.data.weather.dataSource.local.entity.FavoriteLocation
import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteSource
import com.example.weatherforecast.ui.theme.DarkBlue
import com.example.weatherforecast.ui.theme.MidBlue
import com.example.weatherforecast.ui.theme.TextWhite
import com.example.weatherforecast.view.favoritesScreen.viewModel.FavoritesViewModel
import com.example.weatherforecast.view.favoritesScreen.viewModel.FavoritesViewModelFactory
import com.example.weatherforecast.view.mainActivity.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(navController: NavHostController) {

    val appContext = LocalContext.current.applicationContext
    val db = WeatherDatabase.getInstance(appContext)
    val weatherService = RetrofitHelper.weatherService
    val remote = WeatherRemoteSource(weatherService)
    val local = WeatherLocalSource(
        db.currentWeatherDao(),
        db.forecastDao(),
        db.favoriteDao()
    )
    val weatherRepo = WeatherRepo(remote, local)
    val factory = FavoritesViewModelFactory(weatherRepo = weatherRepo)
    val vm: FavoritesViewModel = viewModel(factory = factory)

    val favorites by vm.favorites.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screens.AddFavoriteScreen) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Favorite")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(DarkBlue, MidBlue)))

        ) {
            if (favorites.isEmpty()) {
                EmptyFavoritesView(modifier = Modifier.padding(padding))
            } else {
                ShowTheList(
                    modifier = Modifier
                        .padding(padding),
                    favorites = favorites,
                    navController = navController,
                    vm = vm
                )
            }

        }
    }
}

@Composable
fun ShowTheList(
    modifier: Modifier = Modifier,
    favorites: List<FavoriteLocation>,
    navController: NavHostController,
    vm: FavoritesViewModel,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = favorites,
            key = { "${it.lat}_${it.lon}" }
        ) { favorite ->
            FavoriteCard(
                favorite = favorite,
                onClick = {
                    navController.navigate(
                        Screens.FavoriteDetailScreen(
                            lat = favorite.lat,
                            lon = favorite.lon,
                            cityName = favorite.cityName
                        )
                    )
                },
                onDelete = { vm.deleteFavorite(favorite) }
            )
        }
    }
}

@Composable
fun FavoriteCard(
    favorite: FavoriteLocation,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = favorite.cityName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    text = favorite.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "💧 ${favorite.humidity}%  💨 ${favorite.windSpeed} m/s",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${favorite.iconCode}@2x.png",
                    contentDescription = favorite.description,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "${favorite.temp.toInt()}°",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }

            Image(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                modifier = Modifier.clickable { onDelete() }
            )
        }
    }
}

@Composable
fun EmptyFavoritesView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🌍", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.no_favorite_locations_yet),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TextWhite
            )
            Text(
                text = stringResource(R.string.tap_to_add_one),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}