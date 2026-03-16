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

        //Collectors
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


    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
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
                Toast.makeText(
                    this,
                    message,
                    Toast.LENGTH_LONG
                ).show()

            }
        }
    }

    fun manageLocationState(locationState: LocationResultStates) {
        when (locationState) {
            LocationResultStates.GpsDisabled -> {
                if (true) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.please_enable_gps),
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
                    getString(R.string.could_not_get_location_using_last_known_or_the_default_location),
                    Toast.LENGTH_SHORT
                ).show()
            }

            LocationResultStates.PermissionDenied -> {
                if (isAsked) {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.location_permission_needed_will_use_the_default_location_or_the_last_known),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    isAsked = true
                    locationService.getLocationPermeation()
                }
                AppPreferences.getInstance(this).notifyChanged()
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

    //Collectors
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
