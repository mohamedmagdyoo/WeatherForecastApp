package com.example.weatherforecast.view.mainActivity

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.labs.R
import com.example.weatherforecast.data.appPreferences.AppPreferences
import com.example.weatherforecast.data.location.LocationResultStates
import com.example.weatherforecast.data.location.LocationService
import com.example.weatherforecast.ui.theme.WeatherForecastTheme
import com.example.weatherforecast.utils.language.LanguageHelper
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private companion object {
        var isAsked = false
    }

    private lateinit var appPreferences: AppPreferences
    private lateinit var locationService: LocationService
    private val snackbarHostState = SnackbarHostState()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationService = LocationService(this)
        appPreferences = AppPreferences.getInstance(this)

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            WeatherForecastTheme {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
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

        observeOnLanguageChange()
        observeOnLocationMethodChange()
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val locationState = locationService.tryGetLastLocation()
            manageLocationState(locationState)
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val message =
            getString(R.string.location_permission_needed_will_use_the_default_location_or_the_last_known)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationService.REQUEST_LOCATION_CODE) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                lifecycleScope.launch {
                    val locationState = locationService.tryGetLastLocation()
                    manageLocationState(locationState)
                }
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun manageLocationState(
        locationState: LocationResultStates
    ) {
        when (locationState) {
            LocationResultStates.GpsDisabled -> {
                lifecycleScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = getString(R.string.please_enable_gps),
                        actionLabel = getString(R.string.enable),
                        duration = SnackbarDuration.Long
                    )
                    if (result == ActionPerformed) {
                        locationService.getGpsEnabled()
                    }
                }
            }

            LocationResultStates.LocationNull -> {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.could_not_get_location_using_last_known_or_the_default_location),
                    Toast.LENGTH_SHORT
                ).show()
            }

            LocationResultStates.PermissionDenied -> {
                lifecycleScope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = (getString(R.string.enable_location_permeation)),
                        actionLabel = getString(R.string.enable),
                        duration = SnackbarDuration.Short
                    )

                    if (result == ActionPerformed) {
                        locationService.getLocationPermeation()
                    }

                }
                AppPreferences.getInstance(this).notifyChanged()

//                if (isAsked) {
//                    Toast.makeText(
//                        this@MainActivity,
//                        getString(R.string.location_permission_needed_will_use_the_default_location_or_the_last_known),
//                        Toast.LENGTH_LONG
//                    ).show()
//                } else {
//                    isAsked = true
//                    locationService.getLocationPermeation()
//                }
            }

            is LocationResultStates.Success -> {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.location_fetched_successfully),
                    Toast.LENGTH_SHORT
                )
            }

            LocationResultStates.AlreadySetWithMap -> {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.location_already_set_with_map),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val lang = AppPreferences.getInstance(newBase).getLanguage()
        val newContext = LanguageHelper.setLocale(newBase, lang)
        super.attachBaseContext(newContext)
    }

    fun observeOnLanguageChange() {
        lifecycleScope.launch {
            appPreferences.languageChanged.collect { lang ->
                recreate()
            }
        }
    }

    fun observeOnLocationMethodChange() {
        lifecycleScope.launch {
            appPreferences.locationMethodChanged.collect { locationMethod ->
                if (locationMethod == "GPS") {
                    lifecycleScope.launch {
                        val locationState = locationService.tryGetLastLocation()
                        manageLocationState(locationState)
                    }
                }
            }
        }
    }
}