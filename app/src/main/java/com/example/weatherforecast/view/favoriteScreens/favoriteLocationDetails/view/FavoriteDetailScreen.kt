package com.example.weatherforecast.view.favoriteScreens.favoriteLocationDetails.view


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.network.RetrofitHelper
import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteSource
import com.example.weatherforecast.ui.theme.DarkBlue
import com.example.weatherforecast.ui.theme.MidBlue
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.view.favoriteScreens.favoriteLocationDetails.viewModel.FavoriteDetailState
import com.example.weatherforecast.view.favoriteScreens.favoriteLocationDetails.viewModel.FavoriteDetailViewModel
import com.example.weatherforecast.view.favoriteScreens.favoriteLocationDetails.viewModel.FavoriteDetailViewModelFactory
import com.example.weatherforecast.view.homeScreen.view.OnError
import com.example.weatherforecast.view.homeScreen.view.OnLoading
import com.example.weatherforecast.view.homeScreen.view.OnSuccess

@Composable
fun FavoriteDetailScreen(
    lat: Double,
    lon: Double,
) {
    val appContext = LocalContext.current.applicationContext
    val factory = remember {
        val remote = WeatherRemoteSource(RetrofitHelper.weatherService)
        val appPreferences = AppPreferences.getInstance(appContext)
        FavoriteDetailViewModelFactory(remote, appPreferences)
    }
    val vm: FavoriteDetailViewModel = viewModel(factory = factory)
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        vm.loadData(lat, lon)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBlue, MidBlue)))
    ) {
        Log.d(AppConstants.TAG, "with when: ")
        when (val currentState = state) {
            is FavoriteDetailState.Loading -> OnLoading()

            is FavoriteDetailState.Error -> {
                OnError(
                    onRetry = { vm.loadData(lat, lon) }
                )
            }

            is FavoriteDetailState.Success -> OnSuccess(
                currentWeather = currentState.currentWeather,
                forecast = currentState.forecast,
                prefs = AppPreferences.getInstance(appContext)
            )
        }
    }
}