package com.dexter.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dexter.app.data.datasource.UserDataSourceImpl
import com.dexter.app.data.mapper.UserMapper
import com.dexter.app.data.repository.UserRepositoryImpl
import com.dexter.app.data.mapper.MatchMapper
import com.dexter.app.data.datasource.MatchDataSourceImpl
import com.dexter.app.data.repository.MatchRepositoryImpl
import com.dexter.app.presentation.factory.HomeViewModelFactory
import com.dexter.app.presentation.viewmodel.HomeViewModel
import com.dexter.app.presentation.factory.LoginViewModelFactory
import com.dexter.app.presentation.navigation.NavGraph
import com.dexter.app.presentation.navigation.NavRoutes
import com.dexter.app.presentation.ui.components.BottomNavigationBar
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.LoginViewModel
import com.dexter.app.presentation.ui.layout.MainLayout

class MainActivity : ComponentActivity() {

    // Manual DI chain (same pattern as TaskApp)
    private val loginViewModel: LoginViewModel by viewModels {
        val userMapper = UserMapper()
        val dataSource = UserDataSourceImpl(userMapper)
        val repository = UserRepositoryImpl(dataSource, userMapper)
        LoginViewModelFactory(repository)
    }

    // Home ViewModel (manual DI)
    private val homeViewModel: HomeViewModel by viewModels {
        val matchMapper = MatchMapper()
        val matchDataSource = MatchDataSourceImpl(matchMapper)
        val matchRepository = MatchRepositoryImpl(matchDataSource, matchMapper)
        HomeViewModelFactory(matchRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme(dynamicColor = false) {
                MainScreen(loginViewModel, homeViewModel)
            }
        }
    }
}

/**
 * Root composable that sets up the Scaffold with conditional bottom bar.
 *
 * Follows TaskApp's MainScreen pattern: Scaffold wraps the NavGraph,
 * and the BottomNavigationBar only appears on main-flow screens.
 */
@Composable
fun MainScreen(loginViewModel: LoginViewModel, homeViewModel: HomeViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        NavRoutes.Home.ROUTE,
        NavRoutes.Search.ROUTE,
        NavRoutes.Messages.ROUTE,
        NavRoutes.Profile.ROUTE
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { paddingValues ->
            MainLayout(paddingValues = paddingValues) {
            NavGraph(navController, loginViewModel, homeViewModel, PaddingValues(0.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    AppTheme(dynamicColor = false) {
        // Preview only shows the scaffold structure
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = { BottomNavigationBar(rememberNavController()) }
        ) { }
    }
}