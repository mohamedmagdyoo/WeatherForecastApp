package com.example.weatherforecast.view.favoritesScreen.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.WeatherRepoInterface
import com.example.weatherforecast.data.weather.dataSource.local.entity.FavoriteLocation
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.view.addToFavoriteScreen.viewModel.AddToFavoriteViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val weatherRepo: WeatherRepoInterface,
    private val addToFavViewModel: AddToFavoriteViewModel,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favorites = _favorites.asStateFlow()

    private var hasRefreshed = false

    init {
        // job 1 collect favorites from Room
        viewModelScope.launch {
            weatherRepo.getFavorites().collect { list ->
                _favorites.value = list
                if (!hasRefreshed) {
                    hasRefreshed = true
                    Log.d(AppConstants.TAG, "FavoritesViewModel: refreshFavoritesWeather....")
                    refreshFavoritesWeather(list)
                }
            }
        }

//         job 2 with when language changes independently
        viewModelScope.launch {
            appPreferences.languageChanged.collect {
                refreshFavoritesWeather(_favorites.value)
            }
        }
    }

    private fun refreshFavoritesWeather(list: List<FavoriteLocation>) {
        val unit = appPreferences.getTempUnit()
        val lang = appPreferences.getLanguage()

        list.forEach { favorite ->
            viewModelScope.launch {
                Log.d(
                    AppConstants.TAG,
                    "FavoritesViewModel: refreshFavoritesWeather....${favorite.cityName}"
                )

                addToFavViewModel.saveLocationTODataBase(
                    lat = favorite.lat,
                    lon = favorite.lon,
                    unit = unit,
                    lang = lang
                )
            }
        }
    }

    fun deleteFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            weatherRepo.deleteFavorite(location)
        }
    }
}

@Suppress("UNCHECKED_CAST")
class FavoritesViewModelFactory(
    private val weatherRepo: WeatherRepo,
    private val addToFavViewModel: AddToFavoriteViewModel,
    private val appPreferences: AppPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(weatherRepo, addToFavViewModel, appPreferences) as T
    }
}