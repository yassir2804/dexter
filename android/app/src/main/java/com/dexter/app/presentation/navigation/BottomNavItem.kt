package com.dexter.app.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.dexter.app.R

sealed class BottomNavItem(
    val route: String,
    @StringRes val title: Int,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(
        NavRoutes.Home.ROUTE,
        R.string.nav_home,
        Icons.Default.Home
    )

    data object Search : BottomNavItem(
        NavRoutes.Search.ROUTE,
        R.string.nav_search,
        Icons.Default.Search
    )

    data object Matches : BottomNavItem(
        NavRoutes.Matches.ROUTE,
        R.string.nav_matches,
        Icons.Default.People
    )

    data object Profile : BottomNavItem(
        NavRoutes.Profile.ROUTE,
        R.string.nav_profile,
        Icons.Default.Person
    )

    data object Admin : BottomNavItem(
        NavRoutes.AdminDashboard.ROUTE,
        R.string.nav_admin,
        Icons.Default.AdminPanelSettings
    )

    data object AdminSettingsTab : BottomNavItem(
        NavRoutes.AdminSettings.ROUTE,
        R.string.settings_title,
        Icons.Default.Settings
    )

    companion object {
        fun items(isAdmin: Boolean = false): List<BottomNavItem> {
            return if (isAdmin) listOf(Admin, AdminSettingsTab)
            else listOf(Home, Search, Matches, Profile)
        }
    }
}
