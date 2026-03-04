package com.example.weatherforecast.view.homeScreen.view

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.utils.AppConstants
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val weatherRepo = WeatherRepo()

    GlobalScope.launch {
        val currentWeather = weatherRepo.getCurrentWeather(
            lat = 8.0,
            lon = 8.0
        )

        if(currentWeather.isSuccess){
            val data = currentWeather.getOrNull()
            Log.d(AppConstants.TAG, "HomeScreen: ${data?.name} ")
        }else{
            Log.d(AppConstants.TAG, "HomeScreen: $currentWeather ")
        }

    }


    Column(
        modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "HomeScreen")
    }
}
