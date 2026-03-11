package com.example.weatherforecast.view.addToFavoriteScreen.viewModel

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.WeatherRepoInterface
import com.example.weatherforecast.utils.AppConstants
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class AddToFavoriteViewModel(
    private val context: Context,
    private val weatherRepo: WeatherRepoInterface,
    private val appPreferences: AppPreferences
) : ViewModel() {

    private val _dataStates = MutableStateFlow(AddToFavoriteData())
    val dataStates = _dataStates.asStateFlow()

    private val _state = MutableStateFlow<AddToFavoriteState>(AddToFavoriteState.Idle)
    val state = _state.asStateFlow()


    fun onMapTapped(latLng: LatLng) {
        _dataStates.update { it.copy(selectedLatLan = latLng) }
    }

    fun onSaveClick() {
        if (_dataStates.value.selectedLatLan == LatLng(0.0, 0.0)) {
            _state.value = AddToFavoriteState.UnSelected
        } else {
            _state.value = AddToFavoriteState.Loading
            //here we have to save it into db
            //- just make the logic in repo
            //- need to get favLocation instance
            val unit = appPreferences.getTempUnit()
            val lang = appPreferences.getLanguage()
            val lat = _dataStates.value.selectedLatLan.latitude
            val lon = _dataStates.value.selectedLatLan.longitude

            viewModelScope.launch {
                try {
                    //need fun to get the city name
                    val cityName = getCityName(lat, lon)
                    val result = weatherRepo.getFavoriteLocation(lat, lon, unit, lang, cityName)
                    if (result.isSuccess) {
                        val favLocation = result.getOrThrow()
                        _state.value = AddToFavoriteState.Success
                        weatherRepo.insertFavorite(favLocation)
                    } else {
                        throw result.exceptionOrNull() ?: Exception("Unknown Error")
                    }
                } catch (ex: Exception) {
                    Log.d(AppConstants.TAG, "onSaveClick: ${ex.message}")
                }
            }
        }
    }

    fun getCityName(lat: Double, lon: Double): String {

        val geocoder = Geocoder(context, Locale.getDefault())
        val cityName =
            geocoder.getFromLocation(lat, lon, 1)?.getOrNull(0)?.locality ?: "Unknown city"

        return cityName
    }

}


data class AddToFavoriteData(
    var selectedLatLan: LatLng = LatLng(0.0, 0.0),
)

sealed class AddToFavoriteState {
    object Idle : AddToFavoriteState()
    object Loading : AddToFavoriteState()
    object Success : AddToFavoriteState()
    object UnSelected : AddToFavoriteState()
}


@Suppress("UNCHECKED_CAST")
class AddToFavoriteViewModelFactory(
    private val context: Context,
    private val weatherRepo: WeatherRepoInterface,
    private val appPreferences: AppPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddToFavoriteViewModel(
            context,
            weatherRepo,
            appPreferences
        ) as T
    }
}