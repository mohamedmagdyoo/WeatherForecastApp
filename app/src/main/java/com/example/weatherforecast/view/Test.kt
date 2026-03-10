package com.example.weatherforecast.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.labs.R
import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.ForecastResult
import com.example.weatherforecast.view.homeScreen.view.DailySection
import com.example.weatherforecast.view.homeScreen.view.HeaderSection
import com.example.weatherforecast.view.homeScreen.view.HourlySection
import com.example.weatherforecast.view.homeScreen.view.MainTempCard
import com.example.weatherforecast.view.homeScreen.view.StatsRow
import com.example.weatherforecast.view.homeScreen.view.dummyCurrentWeather
import com.example.weatherforecast.view.homeScreen.view.dummyForecast

private val DarkBlue = Color(0xFF0D1B2A)
private val MidBlue = Color(0xFF1B2F45)
private val AccentBlue = Color(0xFF4FC3F7)
private val CardBg = Color(0xFF1E3248)
private val TextWhite = Color(0xFFECF0F1)
private val TextGrey = Color(0xFF90A4AE)

@Composable
fun OnSuccess(
    modifier: Modifier = Modifier,
    currentWeather: CurrentWeatherEntity,
    forecast: ForecastResult
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(26.dp),
    ) {

        item { HeaderSection(currentWeather) }
        item { MainTempCard(currentWeather) }
        item { StatsRow(currentWeather) }
        if (forecast.listOfHourlyForecast.isNotEmpty()) {
            item { HourlySection(forecast.listOfHourlyForecast) }
        }
        if (forecast.listOfDailyForecast.isNotEmpty()) {
            item { DailySection(forecast.listOfDailyForecast) }
        }
    }
}

@Composable
fun SegmentedOptionRow(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(text = "title")
    }
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
                    modifier = Modifier.align(Alignment.TopStart),
                    text = option,
                    color = if (isSelected) Color.White else TextGrey,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestUiPreview() {
    Box(
        modifier = Modifier
            .background(Brush.verticalGradient(listOf(DarkBlue, MidBlue)))
            .fillMaxSize(),
    ) {

        SegmentedOptionRow(
            options = listOf("Option 1", "Option 2", "Option 3"),
            selected = "Option 2",
            onSelect = {}
        )
    }
}