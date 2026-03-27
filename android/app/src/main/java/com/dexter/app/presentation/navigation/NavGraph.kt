package com.dexter.app.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dexter.app.presentation.session.CurrentUserSession
import com.dexter.app.presentation.ui.screens.LoginScreen
import com.dexter.app.presentation.ui.screens.HomeScreen
import com.dexter.app.presentation.ui.screens.ProfileScreen
import com.dexter.app.presentation.viewmodel.LoginState
import com.dexter.app.presentation.viewmodel.LoginViewModel
import com.dexter.app.presentation.viewmodel.HomeViewModel
import com.dexter.app.presentation.viewmodel.ProfileViewModel

/**
 * Main navigation graph for the app.
 *
 * The NavController lives ONLY here. Screens receive navigation
 * callbacks as lambdas, never the NavController directly.
 * (Professor's navigation guide — "La Regla de Oro")
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    homeViewModel: HomeViewModel,
    profileViewModel: ProfileViewModel,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Login.ROUTE
    ) {
        // ── Auth flow ───────────────────────────────────────────────
        composable(NavRoutes.Login.ROUTE) {
            LoginScreen(
                loginViewModel = loginViewModel,
                onLoginSuccess = {
                    val state = loginViewModel.loginState.value
                    if (state is LoginState.Success) {
                        CurrentUserSession.userId = state.user.id
                    }
                    navController.navigate(NavRoutes.Home.ROUTE) {
                        popUpTo(NavRoutes.Login.ROUTE) { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(NavRoutes.ForgotPassword.ROUTE)
                },
                onRegisterClick = {
                    navController.navigate(NavRoutes.Welcome.ROUTE)
                },
                onGoogleClick = { /* TODO: Google sign-in */ },
                paddingValues = paddingValues
            )
        }

        // ── Main flow ───────────────────────────────────────────────
        composable(NavRoutes.Home.ROUTE) {
            HomeScreen(
                viewModel = homeViewModel,
                onMatchClick = { /* TODO: navigate to match detail */ },
                onActiveSessionClick = { /* TODO: navigate to active session */ }
            )
        }

        composable(NavRoutes.Search.ROUTE) {
            // TODO: SearchScreen
        }

        composable(NavRoutes.Messages.ROUTE) {
            // TODO: MessagesScreen
        }

        composable(NavRoutes.Profile.ROUTE) {
            ProfileScreen(
                profileViewModel = profileViewModel,
                onEditProfileClick = {
                    navController.navigate(NavRoutes.EditProfile.ROUTE)
                }
            )
        }

        // ── Detail screens (placeholders) ───────────────────────────
        composable(NavRoutes.Settings.ROUTE) {
            // TODO: SettingsScreen
        }

        composable(NavRoutes.EditProfile.ROUTE) {
            // TODO: EditProfileScreen
        }
    }
}