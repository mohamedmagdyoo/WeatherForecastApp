package com.example.weatherforecast.view.settingsScreen.view

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labs.R
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.ui.theme.AccentBlue
import com.example.weatherforecast.ui.theme.CardBg
import com.example.weatherforecast.ui.theme.DarkBlue
import com.example.weatherforecast.ui.theme.MidBlue
import com.example.weatherforecast.ui.theme.TextGrey
import com.example.weatherforecast.ui.theme.TextWhite
import com.example.weatherforecast.view.settingsScreen.viewModel.SettingsViewModel
import com.example.weatherforecast.view.settingsScreen.viewModel.SettingsViewModelFactory

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext
    val factory = remember {
        SettingsViewModelFactory(AppPreferences.getInstance(context))
    }
    val vm: SettingsViewModel = viewModel(factory = factory)
    val state by vm.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBlue, MidBlue)))
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Text(
                    stringResource(R.string.settings),
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Temperature Unit
            item {
                SettingsSectionCard(stringResource(R.string.temperature_unit)) {
                    SegmentedOptionRow(
                        options = listOf(
                            R.string.celsius_c,
                            R.string.fahrenheit_f,
                            R.string.kelvin_k
                        ),
                        selected = state.tempUnit,
                        onSelect = { vm.setTempUnit(it) }
                    )
                }
            }

            // Wind Speed
            item {
                SettingsSectionCard(stringResource(R.string.wind_speed)) {
                    SegmentedOptionRow(
                        options = listOf(R.string.m_s, R.string.mph),
                        selected = state.windUnit,
                        onSelect = { vm.setWindUnit(it) }
                    )
                }
            }

            // Language
            item {
                SettingsSectionCard(stringResource(R.string.language)) {
                    SegmentedOptionRow(
                        options = listOf(R.string.english, R.string.arabic),
                        selected = state.language,
                        onSelect = { vm.setLanguage(it) }
                    )
                }
            }

            // Location Source
            item {
                SettingsSectionCard(stringResource(R.string.location_source)) {
                    SegmentedOptionRow(
                        options = listOf(R.string.gps, R.string.map),
                        selected = state.locationSource,
                        onSelect = { vm.setLocationSource(it) }
                    )
                }
            }

            // Notifications
            item {
                SettingsSectionCard(stringResource(R.string.notifications)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.enable_weather_alerts),
                            color = TextWhite,
                            fontSize = 14.sp
                        )
                        Switch(
                            checked = state.notificationsEnabled,
                            onCheckedChange = { vm.setNotificationsEnabled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AccentBlue,
                                uncheckedThumbColor = TextGrey,
                                uncheckedTrackColor = CardBg
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = TextGrey,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun SegmentedOptionRow(
    @StringRes options: List<Int>,
    @StringRes selected: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (isSelected) AccentBlue else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(option) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(option),
                    color = if (isSelected) Color.White else TextGrey,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

