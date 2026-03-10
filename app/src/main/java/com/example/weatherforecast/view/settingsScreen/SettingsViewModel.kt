package com.example.weatherforecast.view.settingsScreen

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.appPreferences.toLanguageApi
import com.example.weatherforecast.data.appPreferences.toLanguageDisplay
import com.example.weatherforecast.data.appPreferences.toTempUnitApi
import com.example.weatherforecast.data.appPreferences.toTempUnitDisplay
import com.example.weatherforecast.data.appPreferences.toWindUnitApi
import com.example.weatherforecast.data.appPreferences.toWindUnitDisplay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(private val prefs: AppPreferences) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        _uiState.value = SettingsUiState(
            tempUnit = prefs.getTempUnit().toTempUnitDisplay(),
            windUnit = prefs.getWindUnit().toWindUnitDisplay(),
            language = prefs.getLanguage().toLanguageDisplay(),
            locationSource = prefs.getLocationMethod(),
            notificationsEnabled = prefs.getNotificationsEnabled()
        )
    }

    fun setTempUnit(@StringRes unitRes: Int) {
        _uiState.update { it.copy(tempUnit = unitRes) }
        viewModelScope.launch { prefs.saveTempUnit(unitRes.toTempUnitApi()) }
    }

    fun setWindUnit(@StringRes unitRes: Int) {
        _uiState.update { it.copy(windUnit = unitRes) }
        viewModelScope.launch { prefs.saveWindUnit(unitRes.toWindUnitApi()) }
    }

    fun setLanguage(@StringRes langRes: Int) {
        _uiState.update { it.copy(language = langRes) }
        viewModelScope.launch { prefs.saveLanguage(langRes.toLanguageApi()) }
    }

    fun setLocationSource(source: String) {
        _uiState.update { it.copy(locationSource = source) }
        viewModelScope.launch { prefs.setLocationMethod(source) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
        viewModelScope.launch { prefs.setNotificationsEnabled(enabled) }
    }
}

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