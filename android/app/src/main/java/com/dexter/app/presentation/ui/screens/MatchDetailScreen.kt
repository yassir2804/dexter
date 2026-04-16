package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import com.dexter.app.presentation.ui.components.PreviewSafeFlowRow
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.MatchDetail
import com.dexter.app.domain.model.MatchStatus
import com.dexter.app.presentation.ui.components.AvatarCircle
import com.dexter.app.presentation.ui.components.CompatibilityRing
import com.dexter.app.presentation.ui.components.SkillChip
import com.dexter.app.presentation.ui.components.TopAppBarDexter
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.ui.theme.StarColor
import com.dexter.app.presentation.viewmodel.MatchDetailState
import com.dexter.app.presentation.viewmodel.MatchDetailViewModel

/**
 * Match Detail — Stateful composable.
 */
@Composable
fun MatchDetailScreen(
    matchId: Long,
    viewModel: MatchDetailViewModel,
    onBackClick: () -> Unit,
    onConnectClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(matchId) {
        viewModel.loadMatch(matchId)
    }

    MatchDetailContent(
        state = state,
        onBackClick = onBackClick,
        onConnectClick = onConnectClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchDetailContent(
    state: MatchDetailState,
    onBackClick: () -> Unit,
    onConnectClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBarDexter(
                title = stringResource(R.string.match_title),
                onBack = onBackClick
            )
        },
        modifier = modifier
    ) { padding ->
        when (state) {
            is MatchDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is MatchDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is MatchDetailState.Success -> {
                MatchDetailBody(
                    match = state.match,
                    onConnectClick = onConnectClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun MatchDetailBody(
    match: MatchDetail,
    onConnectClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedDays = remember { mutableStateOf(match.availability.toSet()) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // ── Header card with avatar + name + rating ─────────────────
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                AvatarCircle(
                    initials = match.initials,
                    imageUrl = match.avatarUrl,
                    size = 80.dp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = match.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = StarColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(
                            R.string.match_sessions_completed,
                            match.rating,
                            match.sessionsCompleted
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Compatibility ring ──────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            CompatibilityRing(percentage = match.compatibilityPercent)
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.match_compatibility),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(20.dp))

        // ── Skills ──────────────────────────────────────────────────
        Text(
            text = stringResource(R.string.match_skills),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            SkillsGroupCard(
                label = stringResource(R.string.match_offers),
                skills = match.offeredSkills,
                isError = false,
                modifier = Modifier.weight(1f)
            )
            SkillsGroupCard(
                label = stringResource(R.string.match_seeks),
                skills = match.soughtSkills,
                isError = true,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(20.dp))

        // ── AI analysis ─────────────────────────────────────────────
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.match_ai_analysis),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = match.aiAnalysis,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Availability ────────────────────────────────────────────
        Text(
            text = stringResource(R.string.match_availability),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(8.dp))
        val dayLabels = listOf(
            R.string.day_mon,
            R.string.day_tue,
            R.string.day_wed,
            R.string.day_thu,
            R.string.day_fri,
            R.string.day_sat,
            R.string.day_sun
        )
        PreviewSafeFlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            dayLabels.forEachIndexed { index, resId ->
                SkillChip(
                    label = stringResource(resId),
                    selected = selectedDays.value.contains(index),
                    onClick = {
                        selectedDays.value = if (selectedDays.value.contains(index)) {
                            selectedDays.value - index
                        } else {
                            selectedDays.value + index
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Connect button ──────────────────────────────────────────
        Button(
            onClick = { onConnectClick(match.matchId) },
            enabled = match.status != MatchStatus.REJECTED && match.status != MatchStatus.CANCELLED,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(R.string.match_connect, match.name),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun SkillsGroupCard(
    label: String,
    skills: List<String>,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            PreviewSafeFlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                skills.forEach { skill ->
                    SkillChip(
                        label = skill,
                        selected = !isError,
                        isError = isError
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MatchDetailLoadingPreview() {
    AppTheme(dynamicColor = false) {
        MatchDetailContent(
            state = MatchDetailState.Loading,
            onBackClick = {},
            onConnectClick = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun MatchDetailSuccessPreview() {
    val sample = MatchDetail(
        matchId = 1L,
        otherUserId = 2L,
        initials = "ML",
        name = "Maria Lopez",
        avatarUrl = null,
        bio = "Diseñadora gráfica apasionada por la música.",
        rating = 4.8,
        sessionsCompleted = 23,
        compatibilityPercent = 95,
        offeredSkills = listOf("Guitarra", "Piano"),
        soughtSkills = listOf("Inglés", "Redacción"),
        availability = listOf(1, 2, 4),
        aiAnalysis = "Tu horario coincide con el de Maria los lunes, martes y jueves.",
        status = MatchStatus.PENDING
    )
    AppTheme(dynamicColor = false) {
        MatchDetailContent(
            state = MatchDetailState.Success(sample),
            onBackClick = {},
            onConnectClick = {}
        )
    }
}
