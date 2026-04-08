package com.dexter.app.presentation.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dexter.app.presentation.session.CurrentUserSession
import com.dexter.app.presentation.ui.screens.ForgotPasswordScreen
import com.dexter.app.presentation.ui.screens.LoginScreen
import com.dexter.app.presentation.ui.screens.HomeScreen
import com.dexter.app.presentation.ui.screens.OnboardingScreen
import com.dexter.app.presentation.ui.screens.ProfileScreen
import com.dexter.app.presentation.ui.screens.WelcomeScreen
import com.dexter.app.presentation.viewmodel.ForgotPasswordViewModel
import com.dexter.app.presentation.viewmodel.LoginState
import com.dexter.app.presentation.viewmodel.LoginViewModel
import com.dexter.app.presentation.viewmodel.HomeViewModel
import com.dexter.app.presentation.viewmodel.ProfileViewModel
import com.dexter.app.presentation.viewmodel.RegisterViewModel

/**
 * Main navigation graph for the app.
 *
 * The NavController lives ONLY here. Screens receive navigation
 * callbacks as lambdas, never the NavController directly.
 * (Professor's navigation guide — "La Regla de Oro")
 *
 * Auth screens are grouped in a nested graph ("auth_graph")
 * following the professor's navigation guide — section 6: "Grafos Anidados".
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
        startDestination = "auth_graph"
    ) {
        // ── Auth flow (nested graph — professor's guide p.8-9) ──────
        navigation(
            startDestination = NavRoutes.Login.ROUTE,
            route = "auth_graph"
        ) {
            composable(NavRoutes.Login.ROUTE) {
                LoginScreen(
                    loginViewModel = loginViewModel,
                    onLoginSuccess = {
                        val state = loginViewModel.loginState.value
                        if (state is LoginState.Success) {
                            CurrentUserSession.userId = state.user.id
                        }
                        navController.navigate(NavRoutes.Home.ROUTE) {
                            popUpTo("auth_graph") { inclusive = true }
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

            composable(NavRoutes.ForgotPassword.ROUTE) {
                val forgotPasswordViewModel: ForgotPasswordViewModel = viewModel()
                ForgotPasswordScreen(
                    forgotPasswordViewModel = forgotPasswordViewModel,
                    onBackToLoginClick = { navController.navigateUp() }
                )
            }

            composable(NavRoutes.Welcome.ROUTE) {
                val registerViewModel: RegisterViewModel = viewModel()
                WelcomeScreen(
                    registerViewModel = registerViewModel,
                    onContinueClick = {
                        navController.navigate(NavRoutes.Onboarding.ROUTE)
                    },
                    onLoginClick = { navController.navigateUp() }
                )
            }

            composable(NavRoutes.Onboarding.ROUTE) {
                val registerViewModel: RegisterViewModel = viewModel()
                OnboardingScreen(
                    registerViewModel = registerViewModel,
                    onFinishClick = {
                        navController.navigate(NavRoutes.Home.ROUTE) {
                            popUpTo("auth_graph") { inclusive = true }
                        }
                    },
                    onBackClick = { navController.navigateUp() }
                )
            }
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
