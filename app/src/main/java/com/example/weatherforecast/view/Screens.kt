package com.example.weatherforecast.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector
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

    companion object {
        val bottomBarItems = listOf(
            BottomBarItem(HomeScreen, "Home", Icons.Filled.Home, Icons.Outlined.Home),
            BottomBarItem(
                FavoritesScreen,
                "Favorites",
                Icons.Filled.Favorite,
                Icons.Outlined.FavoriteBorder
            ),
            BottomBarItem(AlertsScreen, "Alerts", Icons.Filled.Alarm, Icons.Outlined.Alarm),
            BottomBarItem(Settings, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings),
        )
    }
}

data class BottomBarItem(
    val screen: Screens,
    val label: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector
)

