package com.example.weatherforecast.view.homeScreen.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.widget.ProgressBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.view.homeScreen.viewModel.HomeViewModel
import com.example.weatherforecast.view.homeScreen.viewModel.HomeViewModelFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.weatherforecast.data.weather.dataSource.remote.model.forcast.ForecastResult
import com.example.weatherforecast.data.weather.dataSource.remote.model.weather.CurrentWeatherResponse
import com.example.weatherforecast.view.homeScreen.viewModel.HomeScreenState

val weatherRepo = WeatherRepo()
val viewModelFactory =
    HomeViewModelFactory(weatherRepo)

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val vm: HomeViewModel = viewModel(
        factory = viewModelFactory
    )
    val state = vm.screenState.collectAsState().value

    when (val currentState = state) {
        is HomeScreenState.Error -> {
            OnError(modifier, currentState.error)
        }

        HomeScreenState.Loading -> {
            OnLoading(modifier = modifier)
        }

        is HomeScreenState.Success -> {
            OnSuccess(modifier, currentState.currentWeather, currentState.forecastResult)
        }
    }
}

@Composable
fun OnSuccess(
    modifier: Modifier = Modifier,
    currentWeather: CurrentWeatherResponse,
    forecast: ForecastResult
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val firstHourForecast = forecast.listOfHourlyForecast.get(0).temp
        val firsDayForecast = forecast.listOfDailyForecast.get(0).day
        Text(text = "currentWeatherName: ${currentWeather.name}")
        Text(text = "first3HourTemp: $firstHourForecast")
        Text(text = "firstDayName: ${firsDayForecast}")
    }
}

@Composable
fun OnLoading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun OnError(modifier: Modifier = Modifier, error: String) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = error)
    }
}


