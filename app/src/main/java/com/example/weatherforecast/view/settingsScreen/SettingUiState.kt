package com.example.weatherforecast.view.settingsScreen

import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import com.example.labs.R


data class SettingsUiState(
    @StringRes val tempUnit: Int = R.string.celsius_c,
    @StringRes val windUnit: Int = R.string.m_s,
    @StringRes val language: Int = R.string.english,
    @StringRes val locationSource: Int = R.string.gps,
    val notificationsEnabled: Boolean = true
)