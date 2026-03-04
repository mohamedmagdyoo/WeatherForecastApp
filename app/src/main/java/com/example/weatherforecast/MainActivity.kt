package com.example.weatherforecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherforecast.ui.theme.WeatherForecastTheme
import com.example.weatherforecast.view.Screens
import com.example.weatherforecast.view.alertScreen.AlertScreen
import com.example.weatherforecast.view.favoritesScreen.FavScreen
import com.example.weatherforecast.view.homeScreen.view.HomeScreen
import com.example.weatherforecast.view.settingsScreen.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            WeatherForecastTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { WeatherBottomBar(navController) }
                ) { innerPadding ->
                    SetUpNavGraph(Modifier.padding(innerPadding), navController)
                }
            }
        }
    }
}

@Composable
fun SetUpNavGraph(modifier: Modifier = Modifier, navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screens.HomeScreen,
        modifier = modifier
    ) {
        composable<Screens.HomeScreen> {
            HomeScreen(modifier = modifier)
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
    val currentBackStack by navController.currentBackStackEntryAsState() // top screen on back stack as state
    val currentDestination = currentBackStack?.destination

    NavigationBar {
        Screens.bottomBarItems.forEachIndexed { index, item ->
            // let's check if the current screen == this item
            val isSelected = currentDestination?.hasRoute(item.screen::class) == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate((item.screen)) {
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
                        item.label
                    )
                },
                label = { Text(text = item.label) }
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WeatherForecastTheme {
    }
}