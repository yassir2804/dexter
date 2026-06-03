package com.dexter.app.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dexter.app.presentation.session.CurrentUserSession
import com.dexter.app.presentation.ui.layout.MainLayout
import com.dexter.app.presentation.ui.screens.ActiveSessionScreen
import com.dexter.app.presentation.ui.screens.EditSessionScreen
import com.dexter.app.presentation.ui.screens.NotificationRoute
import com.dexter.app.presentation.ui.screens.NotificationsScreen
import com.dexter.app.presentation.ui.screens.RateScreen
import com.dexter.app.presentation.ui.screens.SessionInviteScreen
import com.dexter.app.presentation.ui.screens.UserProfileScreen
import com.dexter.app.presentation.ui.screens.resolveNotificationRoute
import com.dexter.app.presentation.ui.screens.ForgotPasswordScreen
import com.dexter.app.presentation.ui.screens.LoginScreen
import com.dexter.app.presentation.ui.screens.HomeScreen
import com.dexter.app.presentation.ui.screens.MatchDetailScreen
import com.dexter.app.presentation.ui.screens.OnboardingScreen
import com.dexter.app.presentation.ui.screens.PlanScreen
import com.dexter.app.presentation.ui.screens.ProfileScreen
import com.dexter.app.presentation.ui.screens.WelcomeScreen
import com.dexter.app.presentation.viewmodel.ActiveSessionViewModel
import com.dexter.app.presentation.viewmodel.EditSessionViewModel
import com.dexter.app.presentation.viewmodel.NotificationsViewModel
import com.dexter.app.presentation.viewmodel.RateViewModel
import com.dexter.app.presentation.viewmodel.SessionInviteViewModel
import com.dexter.app.presentation.viewmodel.UserProfileViewModel
import com.dexter.app.presentation.viewmodel.ForgotPasswordViewModel
import com.dexter.app.presentation.viewmodel.LoginState
import com.dexter.app.presentation.viewmodel.LoginViewModel
import com.dexter.app.presentation.viewmodel.HomeViewModel
import com.dexter.app.presentation.viewmodel.MatchDetailViewModel
import com.dexter.app.presentation.viewmodel.PlanViewModel
import com.dexter.app.presentation.viewmodel.ProfileViewModel
import com.dexter.app.presentation.viewmodel.ProfileState
import com.dexter.app.presentation.viewmodel.RegisterViewModel
import com.dexter.app.presentation.ui.screens.SettingsScreen
import com.dexter.app.presentation.session.ThemePreference
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dexter.app.presentation.viewmodel.SearchViewModel
import com.dexter.app.presentation.ui.screens.SearchScreen
import com.dexter.app.presentation.ui.screens.MatchesScreen
import com.dexter.app.presentation.ui.screens.PeopleListScreen
import com.dexter.app.presentation.ui.screens.EditProfileScreen

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
    searchViewModel: SearchViewModel,
    paddingValues: PaddingValues,
    userName: String = ""
) {
    NavHost(
        navController = navController,
        startDestination = "auth_graph",
        enterTransition = { fadeIn(animationSpec = tween(220)) },
        exitTransition = { fadeOut(animationSpec = tween(180)) },
        popEnterTransition = { fadeIn(animationSpec = tween(220)) },
        popExitTransition = { fadeOut(animationSpec = tween(180)) }
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
                val registerViewModel: RegisterViewModel = hiltViewModel()
                WelcomeScreen(
                    registerViewModel = registerViewModel,
                    onContinueClick = {
                        // Pass email & password as nav arguments to Onboarding
                        val email = registerViewModel.email.value
                        val password = registerViewModel.password.value
                        navController.navigate(
                            NavRoutes.Onboarding.createRoute(email, password)
                        )
                    },
                    onLoginClick = { navController.navigateUp() }
                )
            }

            composable(
                route = NavRoutes.Onboarding.ROUTE,
                arguments = listOf(
                    navArgument(NavRoutes.Onboarding.ARG_EMAIL) { type = NavType.StringType },
                    navArgument(NavRoutes.Onboarding.ARG_PASSWORD) { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val email = backStackEntry.arguments
                    ?.getString(NavRoutes.Onboarding.ARG_EMAIL) ?: ""
                val password = backStackEntry.arguments
                    ?.getString(NavRoutes.Onboarding.ARG_PASSWORD) ?: ""
                val registerViewModel: RegisterViewModel = hiltViewModel()
                OnboardingScreen(
                    registerViewModel = registerViewModel,
                    email = email,
                    password = password,
                    onFinishClick = {
                        // Registration successful — go back to Login
                        navController.navigate(NavRoutes.Login.ROUTE) {
                            popUpTo("auth_graph") { inclusive = true }
                        }
                    },
                    onBackClick = { navController.navigateUp() }
                )
            }
        }

        // ── Main flow ───────────────────────────────────────────────
        composable(NavRoutes.Home.ROUTE) {
            MainLayout(
                paddingValues = paddingValues,
                currentRoute = NavRoutes.Home.ROUTE,
                userName = userName,
                onNotificationsClick = {
                    navController.navigate(NavRoutes.Notifications.ROUTE)
                },
                onSettingsClick = {
                    navController.navigate(NavRoutes.Settings.ROUTE)
                }
            ) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onMatchClick = { match ->
                        if (match.status == "SUGGESTION") {
                            navController.navigate(NavRoutes.UserProfile.createRoute(match.otherUserId))
                        } else {
                            navController.navigate(NavRoutes.MatchDetail.createRoute(match.matchId))
                        }
                    },
                    onActiveSessionClick = {
                        navController.navigate(NavRoutes.ActiveSession.createRoute(13L))
                    }
                )
            }
        }

        composable(NavRoutes.Search.ROUTE) {
            MainLayout(
                paddingValues = paddingValues,
                currentRoute = NavRoutes.Search.ROUTE,
                userName = userName,
                onNotificationsClick = {
                    navController.navigate(NavRoutes.Notifications.ROUTE)
                },
                onSettingsClick = {
                    navController.navigate(NavRoutes.Settings.ROUTE)
                }
            ) {
                SearchScreen(
                    searchViewModel = searchViewModel,
                    onSkillClick = { skillName ->
                        navController.navigate(NavRoutes.PeopleList.createRoute(skillName))
                    }
                )
            }
        }

        // ── People List (detail, no bottom bar) ─────────────────
        composable(
            route = NavRoutes.PeopleList.ROUTE,
            arguments = listOf(
                navArgument(NavRoutes.PeopleList.ARG_SKILL_NAME) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val skillName = backStackEntry.arguments
                ?.getString(NavRoutes.PeopleList.ARG_SKILL_NAME) ?: ""
            PeopleListScreen(
                skillName = skillName,
                onPersonClick = { userId ->
                    navController.navigate(NavRoutes.UserProfile.createRoute(userId, skillName))
                },
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(NavRoutes.Matches.ROUTE) {
            MainLayout(
                paddingValues = paddingValues,
                currentRoute = NavRoutes.Matches.ROUTE,
                userName = userName,
                onNotificationsClick = {
                    navController.navigate(NavRoutes.Notifications.ROUTE)
                },
                onSettingsClick = {
                    navController.navigate(NavRoutes.Settings.ROUTE)
                }
            ) {
                MatchesScreen(
                    viewModel = homeViewModel,
                    onMatchClick = { match ->
                        navController.navigate(NavRoutes.MatchDetail.createRoute(match.matchId))
                    }
                )
            }
        }

        composable(NavRoutes.Profile.ROUTE) {
            MainLayout(
                paddingValues = paddingValues,
                currentRoute = NavRoutes.Profile.ROUTE,
                userName = userName,
                onNotificationsClick = {
                    navController.navigate(NavRoutes.Notifications.ROUTE)
                },
                onSettingsClick = {
                    navController.navigate(NavRoutes.Settings.ROUTE)
                }
            ) {
                ProfileScreen(
                    profileViewModel = profileViewModel,
                    onEditProfileClick = {
                        navController.navigate(NavRoutes.EditProfile.ROUTE)
                    }
                )
            }
        }

        // ── Detail screens (placeholders) ───────────────────────────
        composable(NavRoutes.Settings.ROUTE) {
            val profileState by profileViewModel.profileState.collectAsState()
            val profile = (profileState as? ProfileState.Success)?.profile
            val isDarkMode by ThemePreference.isDarkMode.collectAsState()

            SettingsScreen(
                userName = profile?.let { "${it.firstName} ${it.lastName}" } ?: "",
                userInitials = profile?.initials ?: "",
                userAvatarUrl = profile?.avatarUrl,
                isDarkMode = isDarkMode ?: false,
                onDarkModeToggle = { ThemePreference.toggle(it) },
                onBackClick = { navController.navigateUp() },
                onEditProfileClick = {
                    navController.navigate(NavRoutes.EditProfile.ROUTE)
                },
                onLoggedOut = {
                    loginViewModel.resetState()
                    navController.navigate(NavRoutes.Login.ROUTE) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.EditProfile.ROUTE) {
            // Get current profile from ProfileViewModel (shared via activity scope)
            val profileState by profileViewModel.profileState.collectAsState()
            val profile = (profileState as? ProfileState.Success)?.profile

            if (profile != null) {
                EditProfileScreen(
                    profile = profile,
                    onBackClick = { navController.navigateUp() },
                    onSaved = {
                        // Reload profile with fresh data after PUT, then go back
                        CurrentUserSession.userId?.let { profileViewModel.loadProfile(it) }
                        navController.navigateUp()
                    }
                )
            }
        }

        // ── User Profile ────────────────────────────────────────────
        composable(
            route = NavRoutes.UserProfile.ROUTE,
            arguments = listOf(
                navArgument(NavRoutes.UserProfile.ARG_USER_ID) {
                    type = NavType.LongType
                },
                navArgument(NavRoutes.UserProfile.ARG_SEARCHED_SKILL) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments
                ?.getLong(NavRoutes.UserProfile.ARG_USER_ID) ?: 0L
            val searchedSkill = backStackEntry.arguments
                ?.getString(NavRoutes.UserProfile.ARG_SEARCHED_SKILL)
            val viewModel: UserProfileViewModel = hiltViewModel()
            UserProfileScreen(
                userId = userId,
                searchedSkill = searchedSkill,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() }
            )
        }

        // ── Match Detail ────────────────────────────────────────────
        composable(
            route = NavRoutes.MatchDetail.ROUTE,
            arguments = listOf(
                navArgument(NavRoutes.MatchDetail.ARG_MATCH_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments
                ?.getLong(NavRoutes.MatchDetail.ARG_MATCH_ID) ?: 0L
            val viewModel: MatchDetailViewModel = hiltViewModel()
            MatchDetailScreen(
                matchId = matchId,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onConnectClick = { id ->
                    navController.navigate(NavRoutes.Plan.createRoute(id))
                },
                onRejectDone = { navController.navigateUp() }
            )
        }

        // ── Plan ────────────────────────────────────────────────────
        composable(
            route = NavRoutes.Plan.ROUTE,
            arguments = listOf(
                navArgument(NavRoutes.Plan.ARG_MATCH_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val matchId = backStackEntry.arguments
                ?.getLong(NavRoutes.Plan.ARG_MATCH_ID) ?: 0L
            val viewModel: PlanViewModel = hiltViewModel()
            PlanScreen(
                matchId = matchId,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onEditSession = { sessionId ->
                    navController.navigate(NavRoutes.EditSession.createRoute(sessionId))
                },
                onRateSession = { sessionId ->
                    navController.navigate(NavRoutes.Rate.createRoute(sessionId))
                }
            )
        }

        // ── Edit Session ────────────────────────────────────────────
        composable(
            route = NavRoutes.EditSession.ROUTE,
            arguments = listOf(
                navArgument(NavRoutes.EditSession.ARG_SESSION_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments
                ?.getLong(NavRoutes.EditSession.ARG_SESSION_ID) ?: 0L
            val viewModel: EditSessionViewModel = hiltViewModel()
            EditSessionScreen(
                sessionId = sessionId,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onDoneClick = { navController.navigateUp() }
            )
        }

        // ── Rate ────────────────────────────────────────────────────
        composable(
            route = NavRoutes.Rate.ROUTE,
            arguments = listOf(
                navArgument(NavRoutes.Rate.ARG_SESSION_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments
                ?.getLong(NavRoutes.Rate.ARG_SESSION_ID) ?: 0L
            val viewModel: RateViewModel = hiltViewModel()
            RateScreen(
                sessionId = sessionId,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onDoneClick = { navController.navigateUp() }
            )
        }

        // ── Notifications ───────────────────────────────────────────
        composable(NavRoutes.Notifications.ROUTE) {
            val viewModel: NotificationsViewModel = hiltViewModel()
            NotificationsScreen(
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onNotifClick = { notif ->
                    resolveNotificationRoute(notif)?.let { route ->
                        when (route) {
                            is NotificationRoute.MatchDetail ->
                                navController.navigate(NavRoutes.MatchDetail.createRoute(route.matchId))
                            is NotificationRoute.ActiveSession ->
                                navController.navigate(NavRoutes.ActiveSession.createRoute(route.sessionId))
                            is NotificationRoute.UserProfile ->
                                navController.navigate(NavRoutes.UserProfile.createRoute(route.userId))
                            NotificationRoute.Home ->
                                navController.navigate(NavRoutes.Home.ROUTE) {
                                    popUpTo(NavRoutes.Home.ROUTE) { inclusive = true }
                                }
                        }
                    }
                }
            )
        }

        // ── Session Invite (receive when id>0, send when id==0) ─────
        composable(
            route = NavRoutes.SessionInvite.ROUTE,
            arguments = listOf(
                navArgument(NavRoutes.SessionInvite.ARG_INVITATION_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val invitationId = backStackEntry.arguments
                ?.getLong(NavRoutes.SessionInvite.ARG_INVITATION_ID) ?: 0L
            val viewModel: SessionInviteViewModel = hiltViewModel()
            SessionInviteScreen(
                invitationId = invitationId,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onDoneClick = { navController.navigateUp() }
            )
        }

        // ── Active Session ──────────────────────────────────────────
        composable(
            route = NavRoutes.ActiveSession.ROUTE,
            arguments = listOf(
                navArgument(NavRoutes.ActiveSession.ARG_SESSION_ID) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments
                ?.getLong(NavRoutes.ActiveSession.ARG_SESSION_ID) ?: 0L
            val viewModel: ActiveSessionViewModel = hiltViewModel()
            ActiveSessionScreen(
                sessionId = sessionId,
                viewModel = viewModel,
                onBackClick = { navController.navigateUp() },
                onBackHome = {
                    navController.navigate(NavRoutes.Home.ROUTE) {
                        popUpTo(NavRoutes.Home.ROUTE) { inclusive = true }
                    }
                }
            )
        }
    }
}
