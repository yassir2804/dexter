package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dexter.app.presentation.navigation.BottomNavItem
import com.dexter.app.presentation.ui.theme.AppTheme

/**
 * Bottom navigation bar with the four main destinations.
 *
 * Follows TaskApp's BottomNavigationBar pattern: observes the current
 * back-stack entry, highlights the active destination, and navigates
 * with popUpTo + launchSingleTop + restoreState.
 */
@Composable
fun BottomNavigationBar(navController: NavController, isAdmin: Boolean = false) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        BottomNavItem.items(isAdmin).forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.route == item.route
            } ?: false

            val itemTitle = stringResource(id = item.title)
            val itemDescription = if (isSelected) {
                "$itemTitle, selected"
            } else {
                "$itemTitle, not selected"
            }

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        text = itemTitle,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.semantics {
                    contentDescription = itemDescription
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavigationBarPreview() {
    AppTheme(dynamicColor = false) {
        BottomNavigationBar(navController = rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavigationBarAdminPreview() {
    AppTheme(dynamicColor = false) {
        BottomNavigationBar(navController = rememberNavController(), isAdmin = true)
    }
}
