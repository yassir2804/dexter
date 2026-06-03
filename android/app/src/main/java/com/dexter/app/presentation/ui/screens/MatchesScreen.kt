package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.MatchSummary
import com.dexter.app.presentation.session.CurrentUserSession
import com.dexter.app.presentation.ui.components.MatchCard
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.HomeState
import com.dexter.app.presentation.viewmodel.HomeViewModel

@Composable
fun MatchesScreen(
    viewModel: HomeViewModel,
    onMatchClick: (MatchSummary) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val homeState by viewModel.homeState.collectAsState()

    LaunchedEffect(Unit) {
        val userId = CurrentUserSession.userId ?: 1L
        viewModel.loadMatches(userId)
    }

    MatchesContent(
        homeState = homeState,
        onMatchClick = onMatchClick,
        modifier = modifier
    )
}

@Composable
private fun MatchesContent(
    homeState: HomeState,
    onMatchClick: (MatchSummary) -> Unit = {},
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
                text = stringResource(R.string.matches_no_matches),
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

        is HomeState.Success -> {
            val pending = homeState.pendingMatches
            val accepted = homeState.acceptedMatches
            if (pending.isEmpty() && accepted.isEmpty()) {
                Box(
                    modifier = modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.matches_no_matches),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = modifier.fillMaxSize()) {
                    if (pending.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.matches_pending_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            )
                        }
                        items(pending) { match ->
                            MatchCard(match = match, onClick = { onMatchClick(match) })
                        }
                    }
                    if (accepted.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.matches_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = if (pending.isNotEmpty()) 16.dp else 0.dp, bottom = 12.dp)
                            )
                        }
                        items(accepted) { match ->
                            MatchCard(match = match, onClick = { onMatchClick(match) })
                        }
                    }
                }
            }
        }
    }
}

private fun sampleMatch(id: Long, name: String, initials: String) = MatchSummary(
    matchId = id, otherUserId = id + 10,
    name = name, initials = initials, avatarUrl = null,
    skillsOffered = listOf("Guitarra"), skillsSought = listOf("Inglés"),
    rating = 4.8, totalSessions = 23, compatibilityScore = 95.0,
    status = "ACCEPTED", sessionsCount = 5
)

@Preview(showBackground = true)
@Composable
private fun MatchesScreenSuccessPreview() {
    AppTheme(dynamicColor = false) {
        MatchesContent(
            homeState = HomeState.Success(
                pendingMatches = emptyList(),
                acceptedMatches = listOf(
                    sampleMatch(2L, "Juan Perez", "JP"),
                    sampleMatch(3L, "Ana Ramirez", "AR")
                )
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MatchesScreenEmptyPreview() {
    AppTheme(dynamicColor = false) { MatchesContent(homeState = HomeState.Empty) }
}
