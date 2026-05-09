package com.dexter.app.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.dexter.app.R

/**
 * Sealed class representing each item in the bottom navigation bar.
 *
 * Mirrors TaskApp's BottomNavItem pattern: route, @StringRes title, icon,
 * and a companion [items] helper used by [BottomNavigationBar] to iterate.
 */
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

    data object Profile : BottomNavItem(
        NavRoutes.Profile.ROUTE,
        R.string.nav_profile,
        Icons.Default.Person
    )

    companion object {
        fun items() = listOf(Home, Search, Profile)
    }
}
