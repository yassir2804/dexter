package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
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
import com.dexter.app.R
import com.dexter.app.domain.model.MatchSummary
import com.dexter.app.presentation.session.CurrentUserSession
import com.dexter.app.presentation.ui.components.MatchCard
import com.dexter.app.presentation.ui.components.SearchBar
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.HomeState
import com.dexter.app.presentation.viewmodel.HomeViewModel

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

@Composable
private fun HomeContent(
    homeState: HomeState,
    onMatchClick: (MatchSummary) -> Unit = {},
    onActiveSessionClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    when (homeState) {
        is HomeState.Loading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        is HomeState.Empty -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.home_no_matches),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        is HomeState.Error -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = homeState.message, color = MaterialTheme.colorScheme.error)
        }

        is HomeState.Success -> HomeSuccessContent(
            pendingMatches = homeState.pendingMatches,
            acceptedMatches = homeState.acceptedMatches,
            onMatchClick = onMatchClick,
            modifier = modifier
        )
    }
}

@Composable
private fun HomeSuccessContent(
    pendingMatches: List<MatchSummary>,
    acceptedMatches: List<MatchSummary>,
    onMatchClick: (MatchSummary) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {

        item { SearchBar() }

        // ── Solicitudes recibidas ───────────────────────────────────
        if (pendingMatches.isNotEmpty()) {
            item {
                SectionHeader(
                    title = stringResource(R.string.home_pending_requests),
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                )
            }
            items(pendingMatches) { match ->
                MatchCard(match = match, onClick = { onMatchClick(match) })
            }
        }

        // ── Mis matches (aceptados) ──────────────────────────────────
        if (acceptedMatches.isNotEmpty()) {
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
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
            }
            items(acceptedMatches) { match ->
                MatchCard(match = match, onClick = { onMatchClick(match) })
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.fillMaxWidth()
    )
}

// ── Previews ────────────────────────────────────────────────────

private fun sampleMatch(id: Long, name: String, initials: String, status: String) = MatchSummary(
    matchId = id, otherUserId = id + 10,
    name = name, initials = initials, avatarUrl = null,
    skillsOffered = listOf("Guitarra"), skillsSought = listOf("Inglés"),
    rating = 4.8, totalSessions = 23, compatibilityScore = 95.0,
    status = status, sessionsCount = 5
)

@Preview(showBackground = true)
@Composable
private fun HomeScreenSuccessPreview() {
    AppTheme(dynamicColor = false) {
        HomeContent(
            homeState = HomeState.Success(
                pendingMatches = listOf(sampleMatch(1L, "Maria Lopez", "ML", "PENDING")),
                acceptedMatches = listOf(
                    sampleMatch(2L, "Juan Perez", "JP", "ACCEPTED"),
                    sampleMatch(3L, "Ana Ramirez", "AR", "ACCEPTED")
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenEmptyPreview() {
    AppTheme(dynamicColor = false) { HomeContent(homeState = HomeState.Empty) }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenLoadingPreview() {
    AppTheme(dynamicColor = false) { HomeContent(homeState = HomeState.Loading) }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenErrorPreview() {
    AppTheme(dynamicColor = false) {
        HomeContent(homeState = HomeState.Error("Network error fetching match summaries"))
    }
}
