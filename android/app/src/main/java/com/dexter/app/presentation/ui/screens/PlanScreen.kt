package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.MatchDetail
import com.dexter.app.domain.model.MatchStatus
import com.dexter.app.domain.model.Session
import com.dexter.app.domain.model.SessionStatus
import com.dexter.app.presentation.ui.components.AvatarCircle
import com.dexter.app.presentation.ui.components.PlanSessionItem
import com.dexter.app.presentation.ui.components.SkillChip
import com.dexter.app.presentation.ui.components.TopAppBarDexter
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.PlanState
import com.dexter.app.presentation.viewmodel.PlanViewModel
import java.util.Date

@Composable
fun PlanScreen(
    matchId: Long,
    viewModel: PlanViewModel,
    onBackClick: () -> Unit,
    onEditSession: (Long) -> Unit,
    onRateSession: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val actionError = (state as? PlanState.Success)?.actionError
    LaunchedEffect(actionError) {
        if (actionError != null) {
            snackbarHostState.showSnackbar(actionError)
            viewModel.clearActionError()
        }
    }

    LaunchedEffect(matchId) {
        viewModel.loadPlan(matchId)
    }

    PlanContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onBackClick = onBackClick,
        onEditSession = onEditSession,
        onRateSession = onRateSession,
        onCompleteSession = { sessionId -> viewModel.completeSession(sessionId) },
        onCompletePlan = { viewModel.completePlan { onBackClick() } },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanContent(
    state: PlanState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBackClick: () -> Unit,
    onEditSession: (Long) -> Unit,
    onRateSession: (Long) -> Unit,
    onCompleteSession: (Long) -> Unit = {},
    onCompletePlan: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBarDexter(
                title = stringResource(R.string.plan_title),
                onBack = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        when (state) {
            is PlanState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is PlanState.Error -> {
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
            is PlanState.Success -> {
                PlanBody(
                    match = state.match,
                    sessions = state.sessions,
                    progress = state.progress,
                    nextSessionId = state.nextSessionId,
                    onEditSession = onEditSession,
                    onRateSession = onRateSession,
                    onCompleteSession = onCompleteSession,
                    onCompletePlan = onCompletePlan,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
        }
    }
}

@Composable
private fun PlanBody(
    match: MatchDetail,
    sessions: List<Session>,
    progress: Float,
    nextSessionId: Long?,
    onEditSession: (Long) -> Unit,
    onRateSession: (Long) -> Unit,
    onCompleteSession: (Long) -> Unit = {},
    onCompletePlan: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val completedCount = sessions.count { it.status == SessionStatus.COMPLETED }
    val lastCompleted = sessions.lastOrNull { it.status == SessionStatus.COMPLETED }

    LazyColumn(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { AiRoadmapBanner(name = match.name) }

        item { MatchSummaryHeader(match = match) }

        item {
            Column {
                Text(
                    text = stringResource(R.string.plan_progress),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.plan_sessions_completed,
                        completedCount,
                        sessions.size
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.plan_sessions_roadmap),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(sessions, key = { it.id }) { session ->
            PlanSessionItem(
                session = session,
                isNext = session.id == nextSessionId,
                onEdit = { onEditSession(session.id) },
                onComplete = { onCompleteSession(session.id) }
            )
        }

        if (lastCompleted != null) {
            item {
                FilledTonalButton(
                    onClick = { onRateSession(lastCompleted.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = stringResource(
                            R.string.plan_rate_session,
                            lastCompleted.sessionNumber
                        ),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item {
            OutlinedButton(
                onClick = onCompletePlan,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = stringResource(R.string.plan_finish),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun AiRoadmapBanner(name: String) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    text = stringResource(R.string.plan_ai_roadmap),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = stringResource(R.string.plan_ai_explanation, name),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun MatchSummaryHeader(match: MatchDetail) {
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            AvatarCircle(
                initials = match.initials,
                imageUrl = match.avatarUrl,
                size = 44.dp
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = match.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = stringResource(
                        R.string.plan_exchange,
                        match.initiatorSkill ?: match.offeredSkills.firstOrNull() ?: "",
                        match.receiverSkill ?: match.soughtSkills.firstOrNull() ?: ""
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            SkillChip(
                label = stringResource(R.string.plan_status_active),
                selected = true
            )
        }
    }
}

@Composable
private fun InfoBoxCard(label: String, value: String, modifier: Modifier = Modifier) {
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun PlanSuccessPreview() {
    val sampleMatch = MatchDetail(
        matchId = 1L,
        otherUserId = 2L,
        initials = "ML",
        name = "Maria Lopez",
        avatarUrl = null,
        bio = "",
        rating = 4.8,
        sessionsCompleted = 23,
        compatibilityPercent = 95,
        offeredSkills = listOf("Guitarra"),
        soughtSkills = listOf("Inglés"),
        availability = listOf(1, 2, 4),
        aiAnalysis = "",
        status = MatchStatus.ACCEPTED
    )
    val sampleSessions = (1..5).map { n ->
        Session(
            id = n.toLong(), matchId = 1, skillId = 1, sessionNumber = n,
            title = "Sesión $n", description = "Descripción $n",
            status = if (n <= 2) SessionStatus.COMPLETED else SessionStatus.SCHEDULED,
            scheduledAt = Date(), completedAt = if (n <= 2) Date() else null,
            createdAt = Date()
        )
    }
    AppTheme(dynamicColor = false) {
        PlanContent(
            state = PlanState.Success(
                match = sampleMatch,
                sessions = sampleSessions,
                progress = 0.4f,
                nextSessionId = 3L
            ),
            onBackClick = {},
            onEditSession = {},
            onRateSession = {}
        )
    }
}
