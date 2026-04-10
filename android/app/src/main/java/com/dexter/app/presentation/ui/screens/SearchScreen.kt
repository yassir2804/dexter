package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.dexter.app.domain.model.SkillIconType
import com.dexter.app.domain.model.SkillSummary
import com.dexter.app.presentation.ui.components.SearchBar
import com.dexter.app.presentation.ui.components.SkillListItem
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.SearchState
import com.dexter.app.presentation.viewmodel.SearchViewModel

/**
 * Search screen — Stateful composable.
 *
 * Receives navigation callbacks as lambdas (NOT the NavController).
 * When a skill is clicked, [onSkillClick] triggers navigation to PeopleListScreen.
 */
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    onSkillClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchState by searchViewModel.searchState.collectAsState()
    val query by searchViewModel.query.collectAsState()

    LaunchedEffect(Unit) {
        searchViewModel.loadInitialData()
    }

    SearchContent(
        searchState = searchState,
        query = query,
        onQueryChange = searchViewModel::onQueryChange,
        onCategorySelect = searchViewModel::onCategorySelect,
        onSkillClick = onSkillClick,
        modifier = modifier
    )
}

/**
 * Search content — Stateless composable.
 *
 * Follows the same when(state) pattern as [HomeContent]:
 * Loading → CircularProgressIndicator
 * Success → categories + skill list
 * Empty   → "no results" text
 * Error   → error message
 */
@Composable
private fun SearchContent(
    searchState: SearchState,
    query: String,
    onQueryChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onSkillClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (searchState) {
        is SearchState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is SearchState.Success -> {
            SkillListView(
                categories = searchState.categories,
                skills = searchState.skills,
                selectedCategory = searchState.selectedCategory,
                query = query,
                onQueryChange = onQueryChange,
                onCategorySelect = onCategorySelect,
                onSkillClick = onSkillClick,
                modifier = modifier
            )
        }

        is SearchState.Empty -> {
            Column(modifier = modifier.fillMaxSize()) {
                SearchBar(
                    query = query,
                    onQueryChanged = onQueryChange,
                    onFilterClick = {}
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.search_no_results),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        is SearchState.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = searchState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Main search view showing categories and popular skills.
 */
@Composable
private fun SkillListView(
    categories: List<String>,
    skills: List<SkillSummary>,
    selectedCategory: String,
    query: String,
    onQueryChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onSkillClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = query,
            onQueryChanged = onQueryChange,
            onFilterClick = {}
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Category filter chips — horizontal scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { category ->
                val isSelected = category == selectedCategory
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategorySelect(category) },
                    label = { Text(category) },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else null,
                    shape = RoundedCornerShape(8.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (skills.isNotEmpty()) {
            Text(
                text = stringResource(R.string.search_popular),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Skills list in elevated card
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    skills.forEachIndexed { index, skill ->
                        SkillListItem(
                            skillName = skill.name,
                            peopleCount = skill.peopleCount,
                            iconType = skill.iconType,
                            onClick = { onSkillClick(skill.name) }
                        )
                        if (index < skills.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // No skills for this category/query — show inline message
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.search_no_results),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SearchSuccessPreview() {
    AppTheme(dynamicColor = false) {
        SearchContent(
            searchState = SearchState.Success(
                categories = listOf("Todas", "Idiomas", "Musica", "Tech"),
                skills = listOf(
                    SkillSummary("Guitarra", "Musica", 12, SkillIconType.MUSIC),
                    SkillSummary("Ingles", "Idiomas", 24, SkillIconType.LANGUAGE),
                    SkillSummary("Programacion", "Tech", 18, SkillIconType.CODE)
                ),
                selectedCategory = "Todas"
            ),
            query = "",
            onQueryChange = {},
            onCategorySelect = {},
            onSkillClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchLoadingPreview() {
    AppTheme(dynamicColor = false) {
        SearchContent(
            searchState = SearchState.Loading,
            query = "",
            onQueryChange = {},
            onCategorySelect = {},
            onSkillClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchEmptyPreview() {
    AppTheme(dynamicColor = false) {
        SearchContent(
            searchState = SearchState.Empty,
            query = "xyz",
            onQueryChange = {},
            onCategorySelect = {},
            onSkillClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchErrorPreview() {
    AppTheme(dynamicColor = false) {
        SearchContent(
            searchState = SearchState.Error("Network error fetching skills"),
            query = "",
            onQueryChange = {},
            onCategorySelect = {},
            onSkillClick = {}
        )
    }
}
