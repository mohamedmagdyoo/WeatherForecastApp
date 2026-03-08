package com.example.weatherforecast.view.homeScreen.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.view.homeScreen.viewModel.HomeViewModel
import com.example.weatherforecast.view.homeScreen.viewModel.HomeViewModelFactory
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.db.WeatherDatabase
import com.example.weatherforecast.data.network.RetrofitHelper
import com.example.weatherforecast.data.weather.dataSource.local.WeatherLocalSource
import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteSource
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.ForecastResult
import com.example.weatherforecast.data.weather.dataSource.remote.dto.weather.CurrentWeatherResponse
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.view.homeScreen.viewModel.HomeScreenState

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val appContext = LocalContext.current.applicationContext
    val weatherDao = WeatherDatabase.getInstance(appContext).currentWeatherDao()
    val forecastDao = WeatherDatabase.getInstance(appContext).forecastDao()
    val local = WeatherLocalSource(currentWeatherDao = weatherDao, forecastDao = forecastDao)
    val weatherApiService = RetrofitHelper.weatherService
    val remote = WeatherRemoteSource(weatherApiService)
    val weatherRepo = WeatherRepo(remote, local)
    val appPreferences = AppPreferences.getInstance(appContext)
    val viewModelFactory =
        HomeViewModelFactory(weatherRepo, appPreferences)

    val vm: HomeViewModel = viewModel(
        factory = viewModelFactory
    )
    val state = vm.screenState.collectAsState().value

    when (state) {
        is HomeScreenState.Error -> {
            OnError(modifier, state.error)
        }

        HomeScreenState.Loading -> {
            OnLoading(modifier = modifier)
        }

        is HomeScreenState.Success -> {
            OnSuccess(modifier, state.currentWeather, state.forecastResult)
        }
    }
}

@Composable
fun OnSuccess(
    modifier: Modifier = Modifier,
    currentWeather: CurrentWeatherEntity,
    forecast: ForecastResult
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (forecast.listOfDailyForecast.isNotEmpty() && forecast.listOfHourlyForecast.isNotEmpty()) {
//            Log.d(AppConstants.TAG, "OnSuccess: forecastdayize${forecast.listOfDailyForecast.size}")
//            Log.d(AppConstants.TAG, "OnSuccess: hours${forecast.listOfHourlyForecast.size}")
            val firstHourForecast = forecast.listOfHourlyForecast[0].temp
            val firsDayForecast = forecast.listOfDailyForecast[0].day
//            Log.d(AppConstants.TAG, "OnSuccess:${firsDayForecast.toString()} ")
            Text(text = "currentWeatherName: ${currentWeather.cityName}")
            Text(text = "first3HourTemp: $firstHourForecast")
            Text(text = "firstDayName: ${firsDayForecast}")
        }
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


