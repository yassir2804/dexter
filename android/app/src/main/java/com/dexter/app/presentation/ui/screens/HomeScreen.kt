package com.dexter.app.presentation.ui.screens



import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.dexter.app.R
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.domain.model.Match
import com.dexter.app.domain.repository.UserRepository
import com.dexter.app.presentation.session.CurrentUserSession
import com.dexter.app.presentation.ui.components.MatchCard
import com.dexter.app.presentation.viewmodel.HomeState
import com.dexter.app.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    userId: Long,
    onMatchClick: (Match) -> Unit = {}
) {
    LaunchedEffect(userId) { viewModel.loadMatchesForUser(userId) }

    val state by viewModel.state.collectAsState()

    when (state) {
        is HomeState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HomeState.Empty -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(id = R.string.home_no_matches), style = MaterialTheme.typography.bodyLarge)
            }
        }
        is HomeState.Error -> {
            val message = (state as HomeState.Error).message
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text(text = stringResource(id = R.string.error_with_message, message), color = MaterialTheme.colorScheme.error)
            }
        }
        is HomeState.Success -> {
            val matches = (state as HomeState.Success).matches
            LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
                items(matches) { match ->
                    MatchCard(match = match, initials = "U") { onMatchClick(match) }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val sample = listOf(
        Match(1, 2, 1, 0.86, com.dexter.app.domain.model.MatchStatus.PENDING, java.util.Date()),
        Match(2, 1, 3, 0.74, com.dexter.app.domain.model.MatchStatus.ACCEPTED, java.util.Date())
    )

    MaterialTheme {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 8.dp)) {
            items(sample) { match -> MatchCard(match = match, initials = "U") }
        }
    }
}
