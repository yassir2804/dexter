package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.PersonResult
import com.dexter.app.domain.model.SkillIconType
import com.dexter.app.domain.model.SkillSummary
import com.dexter.app.presentation.ui.components.AiRecommendationBanner
import com.dexter.app.presentation.ui.components.PersonCard
import com.dexter.app.presentation.ui.components.SearchBar
import com.dexter.app.presentation.ui.components.SkillListItem
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.SearchState
import com.dexter.app.presentation.viewmodel.SearchViewModel

/**
 * Search screen — Stateful composable.
 * Receives navigation callbacks as lambdas (NOT the NavController).
 */
@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    onPersonClick: (Long) -> Unit,
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
        onSkillClick = searchViewModel::onSkillClick,
        onPersonClick = onPersonClick,
        onBackToSkills = searchViewModel::onBackToSkills,
        modifier = modifier
    )
}

/**
 * Search content — Stateless composable.
 * Handles two views: skill list and people list.
 */
@Composable
private fun SearchContent(
    searchState: SearchState,
    query: String,
    onQueryChange: (String) -> Unit,
    onCategorySelect: (String) -> Unit,
    onSkillClick: (String) -> Unit,
    onPersonClick: (Long) -> Unit,
    onBackToSkills: () -> Unit,
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

        is SearchState.SkillList -> {
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

        is SearchState.PeopleList -> {
            PeopleListView(
                skillName = searchState.skillName,
                people = searchState.people,
                onPersonClick = onPersonClick,
                onBackClick = onBackToSkills,
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
    }
}

/**
 * People list view for a selected skill.
 */
@Composable
private fun PeopleListView(
    skillName: String,
    people: List<PersonResult>,
    onPersonClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // Header card with back button
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = onBackClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.cd_search_back)
                    )
                }
                Text(
                    text = skillName,
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AiRecommendationBanner(
                    availabilityText = "Lun-Mie 18:00-20:00"
                )
            }

            item {
                Text(
                    text = stringResource(R.string.search_people_count, people.size),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(people, key = { it.userId }) { person ->
                PersonCard(
                    person = person,
                    onClick = { onPersonClick(person.userId) }
                )
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SkillListPreview() {
    AppTheme(dynamicColor = false) {
        SearchContent(
            searchState = SearchState.SkillList(
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
            onSkillClick = {},
            onPersonClick = {},
            onBackToSkills = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PeopleListPreview() {
    AppTheme(dynamicColor = false) {
        SearchContent(
            searchState = SearchState.PeopleList(
                skillName = "Guitarra",
                people = listOf(
                    PersonResult(3, "Sofia Rodriguez", null, "SR",
                        "Guitarra clasica \u00b7 5 a\u00f1os", 4.9, 47,
                        "Lun-Mie 18:00-20:00", 98, "Coincide 100% con tu disponibilidad"),
                    PersonResult(5, "Ana Martinez", null, "AM",
                        "Guitarra acustica \u00b7 3 a\u00f1os", 4.7, 28,
                        "Vie-Sab 10:00-14:00", 72, "Disponibilidad parcial")
                )
            ),
            query = "",
            onQueryChange = {},
            onCategorySelect = {},
            onSkillClick = {},
            onPersonClick = {},
            onBackToSkills = {}
        )
    }
}
