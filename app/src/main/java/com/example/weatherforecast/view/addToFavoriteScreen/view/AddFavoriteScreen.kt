package com.example.weatherforecast.view.addToFavoriteScreen.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.db.WeatherDatabase
import com.example.weatherforecast.data.network.RetrofitHelper
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.dataSource.local.WeatherLocalSource
import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteSource
import com.example.weatherforecast.view.addToFavoriteScreen.viewModel.AddToFavoriteData
import com.example.weatherforecast.view.addToFavoriteScreen.viewModel.AddToFavoriteState
import com.example.weatherforecast.view.addToFavoriteScreen.viewModel.AddToFavoriteViewModel
import com.example.weatherforecast.view.addToFavoriteScreen.viewModel.AddToFavoriteViewModelFactory
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

    val dataState by vm.dataStates.collectAsStateWithLifecycle()
    val screenState by vm.state.collectAsStateWithLifecycle()



    MainScreen(vm, navController, dataState, screenState)

}

@Preview(showBackground = true)
@Composable
fun AddFavoriteScreenPreview() {
    AddFavoriteScreen(NavHostController(LocalContext.current))
}

@Composable
fun MainScreen(
    vm: AddToFavoriteViewModel,
    navController: NavHostController,
    dataState: AddToFavoriteData,
    screenState: AddToFavoriteState
) {
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
            SearchScreen()

            Spacer(modifier = Modifier.weight(1f))

            Button(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp),
                onClick = {
                    vm.onSaveClick()
                }
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
fun SearchScreen() {

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