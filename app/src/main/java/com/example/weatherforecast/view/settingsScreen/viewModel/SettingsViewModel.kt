package com.example.weatherforecast.view.settingsScreen.viewModel

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.labs.R
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.appPreferences.util.toLanguageApi
import com.example.weatherforecast.data.appPreferences.util.toLanguageDisplay
import com.example.weatherforecast.data.appPreferences.util.toLocationSource
import com.example.weatherforecast.data.appPreferences.util.toLocationSourceDisplay
import com.example.weatherforecast.data.appPreferences.util.toTempUnitApi
import com.example.weatherforecast.data.appPreferences.util.toTempUnitDisplay
import com.example.weatherforecast.data.appPreferences.util.toWindUnitApi
import com.example.weatherforecast.data.appPreferences.util.toWindUnitDisplay
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(private val prefs: AppPreferences) : ViewModel() {
    private val _uiDataState = MutableStateFlow(SettingsUiDataState())
    val uiDataState: StateFlow<SettingsUiDataState> = _uiDataState.asStateFlow()

    private val _settingsScreenState =
        MutableStateFlow<SettingsScreenState>(SettingsScreenState.Ideal)
    val settingsScreenState = _settingsScreenState.asStateFlow()

    //1) emit the change
    //2) save the location in sp

    //I make it like that separated from the ui state cause i don't want with each change with the other change to nav to the map screen :)
    private val _selectedSourceOfLocation = MutableStateFlow(R.string.gps)
    val selectedSourceOfLocation = _selectedSourceOfLocation.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        _uiDataState.value = SettingsUiDataState(
            tempUnit = prefs.getTempUnit().toTempUnitDisplay(),
            windUnit = prefs.getWindUnit().toWindUnitDisplay(),
            language = prefs.getLanguage().toLanguageDisplay(),
            locationSource = prefs.getLocationMethod().toLocationSourceDisplay(),
            notificationsEnabled = prefs.getNotificationsEnabled()
        )
    }

    fun setTempUnit(@StringRes unitRes: Int) {
        _uiDataState.update { it.copy(tempUnit = unitRes) }
        viewModelScope.launch { prefs.saveTempUnit(unitRes.toTempUnitApi()) }
    }

    fun setWindUnit(@StringRes unitRes: Int) {
        _uiDataState.update { it.copy(windUnit = unitRes) }
        viewModelScope.launch { prefs.saveWindUnit(unitRes.toWindUnitApi()) }
    }

    fun setLanguage(@StringRes langRes: Int) {
        _settingsScreenState.value = SettingsScreenState.Loading
        _uiDataState.update { it.copy(language = langRes) }
        viewModelScope.launch { prefs.saveLanguage(langRes.toLanguageApi()) }
    }

    fun setLocationSource(source: Int) {
        _settingsScreenState.value = SettingsScreenState.Loading
        _uiDataState.update { it.copy(locationSource = source) }
        viewModelScope.launch { prefs.setLocationMethod(source.toLocationSource()) }
        _selectedSourceOfLocation.value = source// gps , map
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _uiDataState.update { it.copy(notificationsEnabled = enabled) }
        viewModelScope.launch { prefs.setNotificationsEnabled(enabled) }
    }

    //handling map selection
    fun onSaveLocation(theSelectedLatLng: LatLng) {
        _settingsScreenState.value = SettingsScreenState.Loading
        prefs.saveLocationWithLatAndLon(
            lat = theSelectedLatLng.latitude,
            lon = theSelectedLatLng.longitude
        )
        _settingsScreenState.value = SettingsScreenState.Success
    }
}

sealed class SettingsScreenState {
    object Ideal : SettingsScreenState()
    object Loading : SettingsScreenState()
    object Success : SettingsScreenState()

}

data class SettingsUiDataState(
    @StringRes val tempUnit: Int = R.string.celsius_c,
    @StringRes val windUnit: Int = R.string.m_s,
    @StringRes val language: Int = R.string.english,
    @StringRes val locationSource: Int = R.string.gps,
    val notificationsEnabled: Boolean = true
)

class SettingsViewModelFactory(
    private val prefs: AppPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(prefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}