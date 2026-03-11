package com.example.weatherforecast.view.settingsScreen.view

import androidx.annotation.StringRes
import com.example.labs.R


data class SettingsUiState(
    @StringRes val tempUnit: Int = R.string.celsius_c,
    @StringRes val windUnit: Int = R.string.m_s,
    @StringRes val language: Int = R.string.english,
    @StringRes val locationSource: Int = R.string.gps,
    val notificationsEnabled: Boolean = true
)