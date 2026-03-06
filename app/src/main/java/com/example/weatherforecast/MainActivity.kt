package com.example.weatherforecast

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.location.LocationResultStates
import com.example.weatherforecast.data.location.LocationService
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.ui.theme.WeatherForecastTheme
import com.example.weatherforecast.view.Screens
import com.example.weatherforecast.view.alertScreen.AlertScreen
import com.example.weatherforecast.view.favoritesScreen.FavScreen
import com.example.weatherforecast.view.homeScreen.view.HomeScreen
import com.example.weatherforecast.view.homeScreen.viewModel.HomeViewModel
import com.example.weatherforecast.view.homeScreen.viewModel.HomeViewModelFactory
import com.example.weatherforecast.view.settingsScreen.SettingsScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var locationService: LocationService
    private lateinit var appPreferences: AppPreferences
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationService = LocationService(this)
        appPreferences = AppPreferences(this)
        homeViewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(WeatherRepo())
        )[HomeViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            WeatherForecastTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { WeatherBottomBar(navController) }
                ) { innerPadding ->
                    SetUpNavGraph(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        homeViewModel = homeViewModel
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            when (locationService.getLastLocation()) {

                is LocationResultStates.Success -> {
                    val saved = appPreferences.getSavedLocation()
                    if (saved != null) {
                        //todo handle to see if the location changed call if not don't
                        homeViewModel.getScreenData(saved.first, saved.second)
                    } else {
                        useDefaultLocation()
                    }
                }

                LocationResultStates.GpsDisabled -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Please enable GPS",
                        Toast.LENGTH_LONG
                    ).show()
                    //todo handle to nav to setting just one
                    locationService.getGpsEnabled()
                }

                LocationResultStates.PermissionDenied -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Location permission needed",
                        Toast.LENGTH_LONG
                    ).show()
                    locationService.getLocationPermeation()
                }

                LocationResultStates.LocationNull -> {
                    Toast.makeText(
                        this@MainActivity,
                        "Could not get location, using last known",
                        Toast.LENGTH_SHORT
                    ).show()
                    useFallbackLocation()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if (requestCode == LocationService.REQUEST_LOCATION_CODE) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                lifecycleScope.launch {
                    when (locationService.getLastLocation()) {
                        is LocationResultStates.Success -> {
                            val saved = appPreferences.getSavedLocation()
                            if (saved != null) {
                                homeViewModel.getScreenData(saved.first, saved.second)
                            } else {
                                useDefaultLocation()
                            }
                        }

                        else -> useFallbackLocation()
                    }
                }
            } else {
                Toast.makeText(
                    this,
                    "Showing default location",
                    Toast.LENGTH_SHORT
                ).show()
                useFallbackLocation()
            }
        }
    }

    private fun useFallbackLocation() {
        val saved = appPreferences.getSavedLocation()
        if (saved != null) {
            homeViewModel.getScreenData(saved.first, saved.second)
        } else {
            useDefaultLocation()
        }
    }

    private fun useDefaultLocation() {
        Toast.makeText(this, "Showing weather for Cairo", Toast.LENGTH_SHORT).show()
        homeViewModel.getScreenData(DEFAULT_LAT, DEFAULT_LON)
    }

    companion object {
        const val DEFAULT_LAT = 30.0626
        const val DEFAULT_LON = 31.2497
    }
}

@Composable
fun SetUpNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen,
        modifier = modifier
    ) {
        composable<Screens.HomeScreen> {
            HomeScreen(
                modifier = modifier,
//                viewModel = homeViewModel
            )
        }

        composable<Screens.FavoritesScreen> {
            FavScreen(modifier = modifier)
        }

        composable<Screens.AlertsScreen> {
            AlertScreen(modifier = modifier)
        }

        composable<Screens.Settings> {
            SettingsScreen(modifier = modifier)
        }
    }
}

@Composable
fun WeatherBottomBar(navController: NavHostController) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination

    NavigationBar {
        Screens.bottomBarItems.forEachIndexed { index, item ->
            val isSelected = currentDestination?.hasRoute(item.screen::class) == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.screen) {
                            popUpTo(Screens.HomeScreen) {
                                saveState = true
                                inclusive = false
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        if (isSelected) item.selectedIcon else item.unSelectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(text = item.label) }
            )
        }
    }
}