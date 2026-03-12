package com.example.weatherforecast.view.addToFavoriteScreen.view

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.labs.R
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.db.WeatherDatabase
import com.example.weatherforecast.data.network.RetrofitHelper
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.dataSource.local.WeatherLocalSource
import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteSource
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.view.addToFavoriteScreen.viewModel.AddToFavoriteData
import com.example.weatherforecast.view.addToFavoriteScreen.viewModel.AddToFavoriteState
import com.example.weatherforecast.view.addToFavoriteScreen.viewModel.AddToFavoriteViewModel
import com.example.weatherforecast.view.addToFavoriteScreen.viewModel.AddToFavoriteViewModelFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState

@Composable
fun AddFavoriteScreen(navController: NavHostController) {
    //1) show the map first DONE
    //2) on click on the map send the location to the view model Done
    //3) thaw will be the first property in the data class in vm Done
    //3) on click on the map make a marker DONE
    //4) create save btn to save to db the last latlng that he choosed by on click

    val appContext = LocalContext.current.applicationContext
    val db = WeatherDatabase.getInstance(appContext)
    val weatherDao = db.currentWeatherDao()
    val forecastDao = db.forecastDao()
    val favoriteDao = db.favoriteDao()
    val local = WeatherLocalSource(weatherDao, forecastDao, favoriteDao)
    val weatherService = RetrofitHelper.weatherService
    val remote = WeatherRemoteSource(weatherService)
    val repo = WeatherRepo(remote, local)
    val factory = AddToFavoriteViewModelFactory(
        appContext,
        repo,
        AppPreferences.getInstance(appContext)
    )
    val vm = viewModel<AddToFavoriteViewModel>(factory = factory)

    MainMapScreen(vm, navController) {
        vm.onSaveClick()
    }

}

@Preview(showBackground = true)
@Composable
fun AddFavoriteScreenPreview() {
    AddFavoriteScreen(NavHostController(LocalContext.current))
}

@Composable
fun MainMapScreen(
    vm: AddToFavoriteViewModel,
    navController: NavHostController,
    onSaveLocation: (LatLng) -> Unit
) {
    val dataState by vm.dataStates.collectAsStateWithLifecycle()
    val screenState by vm.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // map and search bar
        MapScreen(vm, dataState)

        //fun to check the screen state
        CheckScreenState(navController, screenState)

        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            SearchScreen(vm, dataState)

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                onClick = { onSaveLocation(dataState.selectedLatLan) }
            ) {
                Text(text = "Save To Favorite")
            }
        }
    }
}

@Composable
fun MapScreen(vm: AddToFavoriteViewModel, data: AddToFavoriteData) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        onMapClick = { vm.onMapTapped(it) }
    ) {
        Marker(
            state = MarkerState(position = data.selectedLatLan),
            title = "Selected Location"
        ) { }
    }
}

@Composable
fun SearchScreen(vm: AddToFavoriteViewModel, data: AddToFavoriteData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = data.searchQuery,
            onValueChange = { vm.onSearchQueryChange(it) },
            placeholder = { Text(stringResource(R.string.search_for_a_city)) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.search_icon)
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (data.suggestions.isEmpty())
            Log.d(AppConstants.TAG, "suggestions.isEmpty ")


        if (data.suggestions.isNotEmpty()) {
            val first = data.suggestions.first()
            Log.d(AppConstants.TAG, "SearchScreen:$first ")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                LazyColumn {
                    items(data.suggestions) { prediction ->
                        Text(
                            text = prediction.getFullText(null).toString(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { vm.onSuggestionSelected(prediction) }
                                .padding(16.dp)
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
fun CheckScreenState(
    navController: NavHostController,
    screenState: AddToFavoriteState
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        when (screenState) {

            is AddToFavoriteState.UnSelected -> {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                ) {
                    Text("Please select a location")
                }
            }

            is AddToFavoriteState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            is AddToFavoriteState.Success -> {
                navController.popBackStack()
            }

            else -> {}
        }
    }
}