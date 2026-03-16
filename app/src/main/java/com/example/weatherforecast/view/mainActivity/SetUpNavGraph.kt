package com.example.weatherforecast.view.mainActivity

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.weatherforecast.view.alertScreens.addAlertScreen.view.AddAlertScreen
import com.example.weatherforecast.view.alertScreens.alertScreen.view.AlertScreen
import com.example.weatherforecast.view.favoriteScreens.addToFavoriteScreen.view.AddFavoriteScreen
import com.example.weatherforecast.view.favoriteScreens.favoriteLocationDetails.view.FavoriteDetailScreen
import com.example.weatherforecast.view.favoriteScreens.favoriteScreen.view.FavoritesScreen
import com.example.weatherforecast.view.homeScreen.view.HomeScreen
import com.example.weatherforecast.view.settingsScreen.view.SettingsScreen

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
            HomeScreen()
        }

        composable<Screens.FavoritesScreen> {
            FavoritesScreen(navController = navController)
        }

        composable<Screens.AddFavoriteScreen> {
            AddFavoriteScreen(navController = navController)
        }

        composable<Screens.FavoriteDetailScreen> { backStackEntry ->
            val args = backStackEntry.toRoute<Screens.FavoriteDetailScreen>()
            FavoriteDetailScreen(
                lat = args.lat,
                lon = args.lon
            )
        }

        composable<Screens.AlertsScreen> {
            AlertScreen(navController)
        }

        composable<Screens.AddAlertScreen> {
            AddAlertScreen(navController)
        }

        composable<Screens.Settings> {
            SettingsScreen(navController)
        }
    }
}

