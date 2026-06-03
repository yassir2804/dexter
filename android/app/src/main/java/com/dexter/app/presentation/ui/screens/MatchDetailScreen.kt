package com.dexter.app.presentation.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically

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
import com.dexter.app.domain.model.MatchDetail
import com.dexter.app.domain.model.MatchStatus
import com.dexter.app.presentation.ui.components.AvatarCircle
import com.dexter.app.presentation.ui.components.CompatibilityRing
import com.dexter.app.presentation.ui.components.SkillChip
import com.dexter.app.presentation.ui.components.TopAppBarDexter
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.ui.theme.StarColor
import com.dexter.app.presentation.session.CurrentUserSession
import com.dexter.app.presentation.viewmodel.AcceptPickerData
import com.dexter.app.presentation.viewmodel.MatchActionState
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
    onRejectDone: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val actionState by viewModel.actionState.collectAsState()
    val acceptPickerData by viewModel.acceptPickerData.collectAsState()

    LaunchedEffect(matchId) { viewModel.loadMatch(matchId) }

    LaunchedEffect(actionState) {
        when (actionState) {
            is MatchActionState.AcceptDone -> {
                viewModel.resetActionState()
                onConnectClick(matchId)
            }
            is MatchActionState.RejectDone -> {
                viewModel.resetActionState()
                onRejectDone()
            }
            else -> Unit
        }
    }

    MatchDetailContent(
        state = state,
        actionState = actionState,
        acceptPickerData = acceptPickerData,
        onBackClick = onBackClick,
        onConnectClick = onConnectClick,
        onAcceptTapped = {
            viewModel.onAcceptTapped(matchId, CurrentUserSession.userId ?: 0L)
        },
        onReject = { viewModel.rejectMatch(matchId) },
        onAcceptPickerSelect = { viewModel.updateAcceptPickerSelection(it) },
        onAcceptPickerConfirm = {
            viewModel.confirmAccept(matchId, CurrentUserSession.userId ?: 0L)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MatchDetailContent(
    state: MatchDetailState,
    actionState: MatchActionState,
    acceptPickerData: AcceptPickerData?,
    onBackClick: () -> Unit,
    onConnectClick: (Long) -> Unit,
    onAcceptTapped: () -> Unit,
    onReject: () -> Unit,
    onAcceptPickerSelect: (String) -> Unit,
    onAcceptPickerConfirm: () -> Unit,
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
            is MatchDetailState.Loading -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            is MatchDetailState.Error -> Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { Text(text = state.message, color = MaterialTheme.colorScheme.error) }

            is MatchDetailState.Success -> MatchDetailBody(
                match = state.match,
                actionState = actionState,
                acceptPickerData = acceptPickerData,
                onConnectClick = onConnectClick,
                onAcceptTapped = onAcceptTapped,
                onReject = onReject,
                onAcceptPickerSelect = onAcceptPickerSelect,
                onAcceptPickerConfirm = onAcceptPickerConfirm,
                modifier = Modifier.fillMaxSize().padding(padding)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun MatchDetailBody(
    match: MatchDetail,
    actionState: MatchActionState,
    acceptPickerData: AcceptPickerData?,
    onConnectClick: (Long) -> Unit,
    onAcceptTapped: () -> Unit,
    onReject: () -> Unit,
    onAcceptPickerSelect: (String) -> Unit,
    onAcceptPickerConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
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

        // ── Contextual compatibility message ────────────────────────
        val infoMessage = when {
            match.canLearnFromOther.isNotEmpty() ->
                stringResource(
                    R.string.match_info_can_learn,
                    match.canLearnFromOther.joinToString(", "),
                    match.name,
                    match.offeredSkills.joinToString(", ")
                )
            match.offeredSkills.isNotEmpty() ->
                stringResource(
                    R.string.match_info_choose,
                    match.name,
                    match.offeredSkills.joinToString(", ")
                )
            else ->
                stringResource(
                    R.string.match_info_seeking,
                    match.name,
                    match.soughtSkills.joinToString(", ")
                )
        }
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = infoMessage,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        // ── Availability (read-only — shows the other user's schedule) ─
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
                    selected = match.availability.contains(index),
                    onClick = {}
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Action buttons ──────────────────────────────────────────
        val isActioning = actionState is MatchActionState.Loading || actionState is MatchActionState.AcceptDone || actionState is MatchActionState.RejectDone
        when (match.status) {
            MatchStatus.PENDING -> {
                // Skill picker for receiver B (shown when B needs to pick what they want to learn)
                AnimatedVisibility(
                    visible = acceptPickerData != null,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    acceptPickerData?.let { picker ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.picker_what_to_learn),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                                Spacer(Modifier.height(12.dp))
                                PreviewSafeFlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    picker.options.forEach { skill ->
                                        SkillChip(
                                            label = skill,
                                            selected = skill == picker.selected,
                                            onClick = { onAcceptPickerSelect(skill) }
                                        )
                                    }
                                }
                                Spacer(Modifier.height(14.dp))
                                Button(
                                    onClick = onAcceptPickerConfirm,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text(
                                        text = stringResource(R.string.picker_learn_button, picker.selected),
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onReject,
                        enabled = !isActioning,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.match_reject),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = onAcceptTapped,
                        enabled = !isActioning && acceptPickerData == null,
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        if (isActioning) CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        ) else Text(
                            text = stringResource(R.string.match_accept),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            MatchStatus.ACCEPTED -> {
                Button(
                    onClick = { onConnectClick(match.matchId) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = stringResource(R.string.match_view_plan),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            else -> Unit
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
            actionState = MatchActionState.Idle,
            acceptPickerData = null,
            onBackClick = {},
            onConnectClick = {},
            onAcceptTapped = {},
            onReject = {},
            onAcceptPickerSelect = {},
            onAcceptPickerConfirm = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun MatchDetailSuccessPreview() {
    val sample = MatchDetail(
        matchId = 1L, otherUserId = 2L, initials = "ML", name = "Maria Lopez",
        avatarUrl = null, bio = "Diseñadora gráfica apasionada por la música.",
        rating = 4.8, sessionsCompleted = 23, compatibilityPercent = 95,
        offeredSkills = listOf("Guitarra", "Piano"),
        soughtSkills = listOf("Inglés", "Redacción"),
        availability = listOf(1, 2, 4),
        aiAnalysis = "",
        status = MatchStatus.PENDING,
        initiatorSkill = null,
        isReceiver = true,
        canLearnFromOther = listOf("Guitarra", "Piano")
    )
    AppTheme(dynamicColor = false) {
        MatchDetailContent(
            state = MatchDetailState.Success(sample),
            actionState = MatchActionState.Idle,
            acceptPickerData = AcceptPickerData(
                options = listOf("Guitarra", "Piano"),
                selected = "Guitarra"
            ),
            onBackClick = {},
            onConnectClick = {},
            onAcceptTapped = {},
            onReject = {},
            onAcceptPickerSelect = {},
            onAcceptPickerConfirm = {}
        )
    }
}
