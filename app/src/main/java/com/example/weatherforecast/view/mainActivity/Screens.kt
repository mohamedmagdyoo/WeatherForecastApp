package com.example.weatherforecast.view.mainActivity

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import com.example.labs.R
import kotlinx.serialization.Serializable

sealed class Screens {
    @Serializable
    object HomeScreen : Screens()

    @Serializable
    object FavoritesScreen : Screens()

    @Serializable
    object AlertsScreen : Screens()

    @Serializable
    object Settings : Screens()

    @Serializable
    object AddFavoriteScreen : Screens()

    @Serializable
    data class FavoriteDetailScreen(
        val lat: Double,
        val lon: Double,
        val cityName: String
    ) : Screens()


    companion object {
        val bottomBarItems = listOf(
            BottomBarItem(
                screen = HomeScreen,
                label = R.string.home,
                selectedIcon = Icons.Filled.Home,
                unSelectedIcon = Icons.Outlined.Home
            ),
            BottomBarItem(
                screen = FavoritesScreen,
                label = R.string.favorites,
                selectedIcon = Icons.Filled.Favorite,
                unSelectedIcon = Icons.Outlined.FavoriteBorder
            ),
            BottomBarItem(
                screen = AlertsScreen,
                label = R.string.alerts,
                selectedIcon = Icons.Filled.Alarm,
                unSelectedIcon = Icons.Outlined.Alarm
            ),
            BottomBarItem(
                screen = Settings,
                label = R.string.settings,
                selectedIcon = Icons.Filled.Settings,
                unSelectedIcon = Icons.Outlined.Settings
            ),
        )
    }
}

data class BottomBarItem(
    val screen: Screens,
    @StringRes val label: Int,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector
)