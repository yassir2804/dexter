package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dexter.app.R
import com.dexter.app.domain.model.MatchSummary
import com.dexter.app.presentation.session.CurrentUserSession
import com.dexter.app.presentation.ui.components.ActiveSessionBanner
import com.dexter.app.presentation.ui.components.MatchCard
import com.dexter.app.presentation.ui.components.SearchBar
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.HomeState
import com.dexter.app.presentation.viewmodel.HomeViewModel

/**
 * Home screen — Stateful composable.
 *
 * Receives navigation callbacks as lambdas (NOT the NavController).
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onMatchClick: (MatchSummary) -> Unit = {},
    onActiveSessionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val homeState by viewModel.homeState.collectAsState()

    LaunchedEffect(Unit) {
        val userId = CurrentUserSession.userId ?: 1L
        viewModel.loadMatches(userId)
    }

    HomeContent(
        homeState = homeState,
        onMatchClick = onMatchClick,
        onActiveSessionClick = onActiveSessionClick,
        modifier = modifier
    )
}

/**
 * Home content — Stateless composable.
 */
@Composable
private fun HomeContent(
    homeState: HomeState,
    onMatchClick: (MatchSummary) -> Unit = {},
    onActiveSessionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        when (homeState) {
            is HomeState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_no_matches),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is HomeState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = homeState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            is HomeState.Success -> {
                HomeSuccessContent(
                    matches = homeState.matches,
                    onMatchClick = onMatchClick,
                    onActiveSessionClick = onActiveSessionClick
                )
            }
        }
    }
}

/**
 * Content shown when matches loaded successfully.
 * Matches the React prototype (HomeScreen.tsx).
 */
@Composable
private fun HomeSuccessContent(
    matches: List<MatchSummary>,
    onMatchClick: (MatchSummary) -> Unit,
    onActiveSessionClick: () -> Unit
) {
    // ── Search Bar ──────────────────────────────────────────────
    SearchBar()

    // TODO: uncomment when active sessions are implemented
    // ActiveSessionBanner(onClick = onActiveSessionClick)

    // ── "Matches sugeridos" + AI chip ───────────────────────────
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.home_suggested_matches),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.width(10.dp))
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.home_ai_badge),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }

    // ── Match Cards ─────────────────────────────────────────────
    for (match in matches) {
        MatchCard(
            match = match,
            onClick = { onMatchClick(match) }
        )
    }

    // TODO: uncomment when session history is implemented
    // // ── "ACTIVIDAD RECIENTE" section ────────────────────────────
    // Text(
    //     text = stringResource(R.string.home_activity_recent).uppercase(),
    //     style = MaterialTheme.typography.labelSmall.copy(
    //         fontWeight = FontWeight.SemiBold,
    //         letterSpacing = 0.8.sp
    //     ),
    //     color = MaterialTheme.colorScheme.primary,
    //     modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    // )
    // // ── Activity Card (FilledCard) ──────────────────────────────
    // Surface(
    //     shape = RoundedCornerShape(12.dp),
    //     color = MaterialTheme.colorScheme.surfaceVariant,
    //     modifier = Modifier
    //         .fillMaxWidth()
    //         .padding(bottom = 16.dp)
    // ) {
    //     Row(
    //         verticalAlignment = Alignment.CenterVertically,
    //         modifier = Modifier.padding(16.dp)
    //     ) {
    //         Surface(
    //             shape = RoundedCornerShape(12.dp),
    //             color = MaterialTheme.colorScheme.secondaryContainer,
    //             modifier = Modifier.size(40.dp)
    //         ) {
    //             Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
    //                 Icon(
    //                     imageVector = Icons.Default.Check,
    //                     contentDescription = null,
    //                     modifier = Modifier.size(20.dp),
    //                     tint = MaterialTheme.colorScheme.onSecondaryContainer
    //                 )
    //             }
    //         }
    //         Spacer(modifier = Modifier.width(12.dp))
    //         Column {
    //             Text(
    //                 text = stringResource(R.string.home_session_completed),
    //                 style = MaterialTheme.typography.titleSmall,
    //                 color = MaterialTheme.colorScheme.onSurface
    //             )
    //             Text(
    //                 text = stringResource(R.string.home_session_completed_time),
    //                 style = MaterialTheme.typography.bodySmall,
    //                 color = MaterialTheme.colorScheme.onSurfaceVariant
    //             )
    //         }
    //     }
    // }
}

// ── Previews ────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun HomeScreenSuccessPreview() {
    val sampleMatches = listOf(
        MatchSummary("ML", "Maria Lopez", null, "Guitarra", "Inglés", "4.8", 23, "95%"),
        MatchSummary("JP", "Juan Perez", null, "Programación", "Diseño gráfico", "4.2", 15, "87%"),
        MatchSummary("AR", "Ana Ramirez", null, "Fotografía", "Marketing", "4.9", 31, "82%")
    )
    AppTheme(dynamicColor = false) {
        HomeContent(homeState = HomeState.Success(sampleMatches))
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    AppTheme(dynamicColor = false) {
        HomeContent(homeState = HomeState.Empty)
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenLoadingPreview() {
    AppTheme(dynamicColor = false) {
        HomeContent(homeState = HomeState.Loading)
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenErrorPreview() {
    AppTheme(dynamicColor = false) {
        HomeContent(homeState = HomeState.Error("Network error fetching match summaries"))
    }
}
