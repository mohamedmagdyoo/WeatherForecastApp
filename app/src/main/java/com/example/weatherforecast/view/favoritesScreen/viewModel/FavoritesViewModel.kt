package com.example.weatherforecast.view.favoritesScreen.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.WeatherRepoInterface
import com.example.weatherforecast.data.weather.dataSource.local.entity.FavoriteLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val weatherRepo: WeatherRepoInterface
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<FavoriteLocation>>(emptyList())
    val favorites = _favorites.asStateFlow()

    init {
        viewModelScope.launch {
            weatherRepo.getFavorites().collect {
                _favorites.value = it
            }
        }
    }

    fun insertFavorite(location: FavoriteLocation) {
        viewModelScope.launch {
            weatherRepo.inertFavorite(location)
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
    private val weatherRepo: WeatherRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavoritesViewModel(weatherRepo) as T
    }
}