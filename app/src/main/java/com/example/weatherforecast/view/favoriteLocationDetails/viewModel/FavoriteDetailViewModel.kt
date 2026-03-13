package com.example.weatherforecast.view.favoriteLocationDetails.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.local.mappers.toEntity
import com.example.weatherforecast.data.weather.dataSource.local.mappers.toEntityList
import com.example.weatherforecast.data.weather.dataSource.local.mappers.toForecastResult
import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteSourceInterface
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.ForecastResult
import com.example.weatherforecast.utils.AppConstants
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class FavoriteDetailViewModel(
    private val remote: WeatherRemoteSourceInterface,
    private val appPreferences: AppPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow<FavoriteDetailState>(FavoriteDetailState.Loading)
    val state = _state.asStateFlow()

    fun loadData(lat: Double, lon: Double) {
        Log.d(AppConstants.TAG, "FavoriteDetailViewModel: loadData....")
        _state.value = FavoriteDetailState.Loading
        val unit = appPreferences.getTempUnit()
        val lang = appPreferences.getLanguage()

        viewModelScope.launch {
            try {
                // todo understand it again
                coroutineScope {
                    // fetch both at the same time
                    val currentWeatherDeferred =
                        async { remote.getCurrentWeather(lat, lon, unit, lang) }
                    val forecastDeferred = async { remote.getForecast(lat, lon, unit, lang) }

                    val currentWeatherResult = currentWeatherDeferred.await()
                    val forecastResult = forecastDeferred.await()

                    if (currentWeatherResult.isSuccess && forecastResult.isSuccess) {
                        _state.value = FavoriteDetailState.Success(
                            currentWeather = currentWeatherResult.getOrThrow().toEntity(lat, lon),
                            forecast = forecastResult.getOrThrow().toEntityList(lat, lon)
                                .toForecastResult()
                        )
                    } else {
                        Log.d(AppConstants.TAG, "FavoriteDetailViewModel: loadData failed...")
                        _state.value = FavoriteDetailState.Error
                    }
                }
            } catch (e: Exception) {
                Log.d(
                    AppConstants.TAG,
                    "FavoriteDetailViewModel: loadData Exception...${e.message}"
                )
                _state.value = FavoriteDetailState.Error
            }
        }
    }
}

sealed class FavoriteDetailState {
    object Loading : FavoriteDetailState()
    object Error : FavoriteDetailState()
    data class Success(
        val currentWeather: CurrentWeatherEntity,
        val forecast: ForecastResult
    ) : FavoriteDetailState()
}

@Suppress("UNCHECKED_CAST")
class FavoriteDetailViewModelFactory(
    private val remote: WeatherRemoteSourceInterface,
    private val appPreferences: AppPreferences,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteDetailViewModel(remote, appPreferences) as T
    }
}