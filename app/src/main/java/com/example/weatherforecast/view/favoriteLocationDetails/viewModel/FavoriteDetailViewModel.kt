package com.example.weatherforecast.view.favoriteLocationDetails.viewModel

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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoriteDetailViewModel(
    private val remote: WeatherRemoteSourceInterface,
    private val appPreferences: AppPreferences,
    private val lat: Double,
    private val lon: Double
) : ViewModel() {

    private val _state = MutableStateFlow<FavoriteDetailState>(FavoriteDetailState.Loading)
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                val unit = appPreferences.getTempUnit()
                val lang = appPreferences.getLanguage()

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
                    _state.value = FavoriteDetailState.Error
                }
            } catch (e: Exception) {
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
    private val lat: Double,
    private val lon: Double
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoriteDetailViewModel(remote, appPreferences, lat, lon) as T
    }
}