package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dexter.app.R
import com.dexter.app.domain.model.PersonResult
import com.dexter.app.presentation.ui.components.PersonCard
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.PeopleListState
import com.dexter.app.presentation.viewmodel.PeopleListViewModel

/**
 * People list screen — Stateful composable.
 *
 * Shows people who offer a specific skill.
 * Receives navigation callbacks as lambdas (NOT the NavController).
 *
 * Uses its own [Scaffold] + [TopAppBar] for status-bar padding and header,
 * following the same layout approach as MainLayout but without the bottom bar.
 *
 * The ViewModel is scoped to this navigation destination via [hiltViewModel],
 * so it is created fresh each time the user navigates here.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleListScreen(
    skillName: String,
    onPersonClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    viewModel: PeopleListViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val peopleListState by viewModel.peopleListState.collectAsState()

    LaunchedEffect(skillName) {
        viewModel.loadPeople(skillName)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = skillName,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_search_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        PeopleListContent(
            peopleListState = peopleListState,
            onPersonClick = onPersonClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * People list content — Stateless composable.
 *
 * Follows the same when(state) pattern as [HomeContent]:
 * Loading → CircularProgressIndicator
 * Success → list of PersonCard items
 * Empty   → "no results" text
 * Error   → error message
 */
@Composable
private fun PeopleListContent(
    peopleListState: PeopleListState,
    onPersonClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    when (peopleListState) {
        is PeopleListState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is PeopleListState.Success -> {
            PeopleListSuccessContent(
                people = peopleListState.people,
                onPersonClick = onPersonClick,
                modifier = modifier
            )
        }

        is PeopleListState.Empty -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.search_no_results),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        is PeopleListState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = peopleListState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Content shown when people loaded successfully.
 * Uses LazyColumn for efficient list rendering (same as HomeSuccessContent).
 * Applies 16.dp horizontal padding to match MainLayout's content padding.
 */
@Composable
private fun PeopleListSuccessContent(
    people: List<PersonResult>,
    onPersonClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(people, key = { it.userId }) { person ->
            PersonCard(
                person = person,
                onClick = { onPersonClick(person.userId) }
            )
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PeopleListSuccessPreview() {
    val samplePeople = listOf(
        PersonResult(3, "Sofia Rodriguez", null, "SR",
            "Guitarra clasica · 5 años", 4.9, 47,
            "Lun-Mie 18:00-20:00", 98, "Coincide 100% con tu disponibilidad"),
        PersonResult(5, "Ana Martinez", null, "AM",
            "Guitarra acustica · 3 años", 4.7, 28,
            "Vie-Sab 10:00-14:00", 72, "Disponibilidad parcial")
    )
    AppTheme(dynamicColor = false) {
        PeopleListContent(
            peopleListState = PeopleListState.Success("Guitarra", samplePeople),
            onPersonClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PeopleListLoadingPreview() {
    AppTheme(dynamicColor = false) {
        PeopleListContent(
            peopleListState = PeopleListState.Loading,
            onPersonClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PeopleListEmptyPreview() {
    AppTheme(dynamicColor = false) {
        PeopleListContent(
            peopleListState = PeopleListState.Empty,
            onPersonClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PeopleListErrorPreview() {
    AppTheme(dynamicColor = false) {
        PeopleListContent(
            peopleListState = PeopleListState.Error("Network error fetching people"),
            onPersonClick = {}
        )
    }
}
