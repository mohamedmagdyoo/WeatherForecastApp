package com.example.weatherforecast.view.addToFavoriteScreen.viewModel

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.labs.BuildConfig
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.WeatherRepoInterface
import com.example.weatherforecast.utils.AppConstants
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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


    // this something like retrofit but just for places
    private val placesClient by lazy {
        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.MAPS_API_KEY)
        }
        Places.createClient(context)
    }

    fun onSearchQueryChange(query: String) {
        _dataStates.update { it.copy(searchQuery = query) }
        if (query.length < 2) {
            _dataStates.update { it.copy(suggestions = emptyList()) }
            return
        }
        viewModelScope.launch {
            try {
                val request = FindAutocompletePredictionsRequest.builder()
                    .setQuery(query)
                    .build()
                val response = placesClient.findAutocompletePredictions(request).await()
                _dataStates.update { it.copy(suggestions = response.autocompletePredictions) }
            } catch (e: Exception) {
                Log.d(AppConstants.TAG, "onSearchQueryChange: ${e.message}")
                _dataStates.update { it.copy(suggestions = emptyList()) }
            }
        }
    }

    fun onSuggestionSelected(prediction: AutocompletePrediction) {
        viewModelScope.launch {
            try {
                //Tell the api what i want to get
                val placeFields = listOf(Place.Field.LAT_LNG, Place.Field.NAME)
                val request = FetchPlaceRequest.newInstance(prediction.placeId, placeFields)
                val response = placesClient.fetchPlace(request).await()
                val place = response.place

                place.latLng?.let { latLng ->
                    _dataStates.update {
                        it.copy(
                            selectedLatLan = latLng,
                            searchQuery = place.name ?: it.searchQuery,
                            suggestions = emptyList()
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d(AppConstants.TAG, "onSuggestionSelected: ${e.message}")
            }
        }
    }

}


data class AddToFavoriteData(
    var selectedLatLan: LatLng = LatLng(0.0, 0.0),
    val searchQuery: String = "",
    val suggestions: List<AutocompletePrediction> = emptyList()
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