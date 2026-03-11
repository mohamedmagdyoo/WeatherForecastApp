package com.example.weatherforecast.view.homeScreen.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.labs.R
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.db.WeatherDatabase
import com.example.weatherforecast.data.network.RetrofitHelper
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.dataSource.local.WeatherLocalSource
import com.example.weatherforecast.data.weather.dataSource.local.entity.CurrentWeatherEntity
import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteSource
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.DailyForecast
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.ForecastResult
import com.example.weatherforecast.data.weather.dataSource.remote.dto.forcast.HourlyForecast
import com.example.weatherforecast.ui.theme.AccentBlue
import com.example.weatherforecast.ui.theme.CardBg
import com.example.weatherforecast.ui.theme.DarkBlue
import com.example.weatherforecast.ui.theme.MidBlue
import com.example.weatherforecast.ui.theme.Pink40
import com.example.weatherforecast.ui.theme.TextGrey
import com.example.weatherforecast.ui.theme.TextWhite
import com.example.weatherforecast.view.homeScreen.viewModel.HomeScreenState
import com.example.weatherforecast.view.homeScreen.viewModel.HomeViewModel
import com.example.weatherforecast.view.homeScreen.viewModel.HomeViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

lateinit var prefs: AppPreferences

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    //That will not ever change so no worry about recomposition
    val appContext = LocalContext.current.applicationContext

    // the key in remember means if this key changes, this remember block will be called again --> remember(key = ..)
    val factory = remember {
        val db = WeatherDatabase.getInstance(appContext)
        val local = WeatherLocalSource(db.currentWeatherDao(),  db.forecastDao(),db.favoriteDao())
        val remote = WeatherRemoteSource(RetrofitHelper.weatherService)
        prefs = AppPreferences.getInstance(appContext)
        val repo = WeatherRepo(remote, local)
        HomeViewModelFactory(repo, prefs)
    }
    val vm: HomeViewModel = viewModel(factory = factory)

    //Will not collect in on(stop,destroy)
    val state by vm.screenState.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBlue, MidBlue)))
    ) {
        when (val currentState = state) {
            is HomeScreenState.Loading -> OnLoading()

            is HomeScreenState.Error -> OnError() {
                vm.enforceRefresh()
            }

            is HomeScreenState.Success -> OnSuccess(
                currentWeather = currentState.currentWeather,
                forecast = currentState.forecastResult
            )
        }
    }
}


// ── Loading ───────────────────────────────────────────────
@Composable
fun OnLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = AccentBlue)
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.fetching_weather), color = TextGrey, fontSize = 13.sp)
        }
    }
}

// ── Error ─────────────────────────────────────────────────
@Composable
fun OnError(
    modifier: Modifier = Modifier,
    message: String = "We couldn't fetch the latest weather data.\n" +
            "Please check your connection or try again\n" +
            "later.",
    onRetry: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.error_ic_svg),
                contentDescription = "Error",
                modifier = Modifier.size(220.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Something went wrong",
                color = TextWhite,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = message,
                color = TextWhite,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            Button(
                modifier = Modifier.size(120.dp, 40.dp),
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Retry", color = Color.White)
            }
        }
    }
}

// ── Success ───────────────────────────────────────────────
@Composable
fun OnSuccess(
    modifier: Modifier = Modifier,
    currentWeather: CurrentWeatherEntity,
    forecast: ForecastResult
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { HeaderSection(currentWeather) }
        item { MainTempCard(currentWeather) }
        item { StatsRow(currentWeather) }
//        if (forecast.listOfHourlyForecast.isNotEmpty()) {
        item { HourlySection(forecast.listOfHourlyForecast) }
//        }
//        if (forecast.listOfDailyForecast.isNotEmpty()) {
        item { DailySection(forecast.listOfDailyForecast) }
//        }
    }
}

// ── Header ────────────────────────────────────────────────
@Composable
fun HeaderSection(weather: CurrentWeatherEntity) {
    val date = remember {
        SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())
    }
    val time = remember {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = weather.cityName,
            color = TextWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "$date  •  $time",
            color = TextGrey,
            fontSize = 13.sp
        )
    }
}

// ── Main Temp Card ────────────────────────────────────────
@Composable
fun MainTempCard(weather: CurrentWeatherEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${weather.icon}@2x.png",
                placeholder = painterResource(id = R.drawable.error_ic_svg),
                contentDescription = weather.description,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = "${weather.temp.toInt()}°",
                color = TextWhite,
                fontSize = 72.sp,
                fontWeight = FontWeight.Thin
            )
            Text(
                text = weather.description.replaceFirstChar { it.uppercase() },
                color = AccentBlue,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.feels_like, weather.feelsLike.toInt()),
                color = TextGrey,
                fontSize = 13.sp
            )
        }
    }
}

// ── Stats Row ─────────────────────────────────────────────

@Composable
fun StatsRow(weather: CurrentWeatherEntity) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item { StatCard("💧", "${weather.humidity}%", stringResource(R.string.humidity)) }
        item {
            val windUnit =
                if (prefs.getWindUnit() == "metric") stringResource(R.string.m_s) else stringResource(
                    R.string.mph
                )

            StatCard(
                "💨", "${"%.2f".format(weather.windSpeed)} $windUnit",
                stringResource(R.string.wind)
            )
        }
        item {

            StatCard("🌡", "${weather.pressure} ⚖", stringResource(R.string.pressure))
        }
        item {
            StatCard("☁️", "${weather.clouds}%", stringResource(R.string.clouds))
        }
    }
}

@Composable
fun StatCard(icon: String, value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .width(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(icon, fontSize = 18.sp)
            Text(value, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(label, color = TextGrey, fontSize = 10.sp)
        }
    }
}

// ── Hourly ────────────────────────────────────────────────
@Composable
fun HourlySection(hourlyList: List<HourlyForecast>) {
    Column {
        Text(
            stringResource(R.string.today),
            color = TextWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(hourlyList) { item -> HourlyItem(item) }
        }
    }
}

@Composable
fun HourlyItem(item: HourlyForecast) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(item.time, color = TextGrey, fontSize = 12.sp)
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${item.icon}.png",
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Text(
                "${item.temp.toInt()}°",
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ── Daily ─────────────────────────────────────────────────
@Composable
fun DailySection(dailyList: List<DailyForecast>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            stringResource(R.string._5_days),
            color = TextWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            dailyList.forEach { item -> DailyItem(item) }
        }
    }
}

@Composable
fun DailyItem(item: DailyForecast) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                item.day,
                color = TextWhite,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
            AsyncImage(
                model = "https://openweathermap.org/img/wn/${item.icon}.png",
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                placeholder = painterResource(R.drawable.error_ic_svg)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "${item.minTemp.toInt()}° / ${item.maxTemp.toInt()}°",
                color = TextGrey,
                fontSize = 13.sp
            )
        }
    }
}