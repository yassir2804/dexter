package com.dexter.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dexter.app.presentation.navigation.NavGraph
import com.dexter.app.presentation.navigation.NavRoutes
import com.dexter.app.presentation.ui.components.BottomNavigationBar
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.HomeViewModel
import com.dexter.app.presentation.viewmodel.LoginState
import com.dexter.app.presentation.viewmodel.LoginViewModel
import com.dexter.app.presentation.viewmodel.NotificationsViewModel
import com.dexter.app.presentation.viewmodel.ProfileViewModel
import com.dexter.app.presentation.viewmodel.SearchViewModel
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import com.dexter.app.presentation.session.ThemePreference
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by viewModels()
    private val searchViewModel: SearchViewModel by viewModels()
    private val notificationsViewModel: NotificationsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkModePref by ThemePreference.isDarkMode.collectAsState()
            val isDark = darkModePref ?: isSystemInDarkTheme()

            AppTheme(dynamicColor = false, darkTheme = isDark) {
                MainScreen(loginViewModel, homeViewModel, profileViewModel, searchViewModel, notificationsViewModel)
            }
        }
    }
}

@Composable
fun MainScreen(
    loginViewModel: LoginViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    searchViewModel: SearchViewModel,
    notificationsViewModel: NotificationsViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        NavRoutes.Home.ROUTE,
        NavRoutes.Search.ROUTE,
        NavRoutes.Matches.ROUTE,
        NavRoutes.Profile.ROUTE,
        NavRoutes.AdminDashboard.ROUTE,
        NavRoutes.AdminSettings.ROUTE
    )

    val loginUser = (loginViewModel.loginState.value as? LoginState.Success)?.user
    val userName = loginUser?.firstName ?: ""
    val isAdmin = loginUser?.roles?.any { it.contains("ADMIN") } ?: false

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                BottomNavigationBar(navController, isAdmin = isAdmin)
            }
        }
    ) { paddingValues ->
        NavGraph(
            navController = navController,
            loginViewModel = loginViewModel,
            homeViewModel = homeViewModel,
            profileViewModel = profileViewModel,
            searchViewModel = searchViewModel,
            notificationsViewModel = notificationsViewModel,
            paddingValues = paddingValues,
            userName = userName
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    AppTheme(dynamicColor = false) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { BottomNavigationBar(rememberNavController()) }
        ) { _: PaddingValues ->
        }
    }
}
