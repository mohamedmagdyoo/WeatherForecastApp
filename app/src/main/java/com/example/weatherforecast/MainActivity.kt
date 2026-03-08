package com.example.weatherforecast

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.db.WeatherDatabase
import com.example.weatherforecast.data.location.LocationResultStates
import com.example.weatherforecast.data.location.LocationService
import com.example.weatherforecast.data.network.RetrofitHelper
import com.example.weatherforecast.data.weather.WeatherRepo
import com.example.weatherforecast.data.weather.dataSource.local.WeatherLocalSource
import com.example.weatherforecast.data.weather.dataSource.remote.WeatherRemoteSource
import com.example.weatherforecast.ui.theme.WeatherForecastTheme
import com.example.weatherforecast.utils.AppConstants
import com.example.weatherforecast.view.Screens
import com.example.weatherforecast.view.alertScreen.AlertScreen
import com.example.weatherforecast.view.favoritesScreen.FavScreen
import com.example.weatherforecast.view.homeScreen.view.HomeScreen
import com.example.weatherforecast.view.homeScreen.viewModel.HomeViewModel
import com.example.weatherforecast.view.homeScreen.viewModel.HomeViewModelFactory
import com.example.weatherforecast.view.settingsScreen.SettingsScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private companion object {
        var isAsked = false
    }
    private lateinit var locationService: LocationService
    private lateinit var appPreferences: AppPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationService = LocationService(this)
        appPreferences = AppPreferences.getInstance(this)

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
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val locationState = locationService.tryGetLastLocation()
            manageLocationState(locationState)
        }
    }


    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationService.REQUEST_LOCATION_CODE) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                Log.d(AppConstants.TAG, "onRequestPermissionsResult: granted ")
                lifecycleScope.launch {
                    val locationState = locationService.tryGetLastLocation()
                    manageLocationState(locationState)
                }
            } else {
                Log.d(AppConstants.TAG, "onRequestPermissionsResult: notGranted ")
                Toast.makeText(
                    this,
                    "Location permission needed, will use the default location or the last known",
                    Toast.LENGTH_LONG
                ).show()

            }
        }
        Log.d(AppConstants.TAG, "onRequestPermissionsResult:REQUEST_LOCATION_CODE  ")


    }

    fun manageLocationState(locationState: LocationResultStates) {
        when (locationState) {
            LocationResultStates.GpsDisabled -> {
                if (isAsked) {
                    Toast.makeText(
                        this@MainActivity,
                        "Please enable GPS",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    isAsked = true
                    locationService.getGpsEnabled()
                }
            }

            LocationResultStates.LocationNull -> {
                Toast.makeText(
                    this@MainActivity,
                    "Could not get location, using last known or the default location",
                    Toast.LENGTH_SHORT
                ).show()
            }

            LocationResultStates.PermissionDenied -> {
                if (isAsked) {
                    Toast.makeText(
                        this@MainActivity,
                        "Location permission needed, will use the default location or the last known",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    isAsked = true
                    locationService.getLocationPermeation()
                }

                appPreferences.notifyChanged()
            }

            is LocationResultStates.Success -> {
                Toast.makeText(
                    this@MainActivity,
                    "Location fetched successfully",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}


@Composable
fun SetUpNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen,
        modifier = modifier
    ) {
        composable<Screens.HomeScreen> {
            HomeScreen(
                modifier = modifier,
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