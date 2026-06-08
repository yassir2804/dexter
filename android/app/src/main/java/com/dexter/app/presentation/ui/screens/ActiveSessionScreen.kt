package com.dexter.app.presentation.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.Session
import com.dexter.app.domain.model.SessionStatus
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.ui.components.AvatarCircle
import com.dexter.app.presentation.ui.components.DexterTextField
import com.dexter.app.presentation.ui.components.InfoRow
import com.dexter.app.presentation.ui.components.SessionTimer
import com.dexter.app.presentation.ui.components.TopAppBarDexter
import com.dexter.app.presentation.ui.theme.OnlineStatusColor
import com.dexter.app.presentation.viewmodel.ActiveSessionState
import com.dexter.app.presentation.viewmodel.ActiveSessionViewModel
import java.util.Date

@Composable
fun ActiveSessionScreen(
    sessionId: Long,
    viewModel: ActiveSessionViewModel,
    onBackClick: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.loadSession(sessionId)
    }

    LaunchedEffect(state) {
        if (state is ActiveSessionState.Ended) {
            onBackHome()
        }
    }

    ActiveSessionContent(
        state = state,
        onBackClick = onBackClick,
        onNotesChange = viewModel::onNotesChange,
        onEndSession = viewModel::onEndSession,
        onBackHome = onBackHome,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActiveSessionContent(
    state: ActiveSessionState,
    onBackClick: () -> Unit,
    onNotesChange: (String) -> Unit,
    onEndSession: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBarDexter(
                title = stringResource(R.string.active_session_title),
                onBack = onBackClick
            )
        },
        modifier = modifier
    ) { padding ->
        when (state) {
            is ActiveSessionState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is ActiveSessionState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            }
            is ActiveSessionState.Active -> {
                ActiveBody(
                    session = state.session,
                    notes = state.notes,
                    ending = state.ending,
                    onNotesChange = onNotesChange,
                    onEndSession = onEndSession,
                    onBackHome = onBackHome,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
            ActiveSessionState.Ended -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
        }
    }
}

@Composable
private fun ActiveBody(
    session: Session,
    notes: String,
    ending: Boolean,
    onNotesChange: (String) -> Unit,
    onEndSession: () -> Unit,
    onBackHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PulsingStatusBadge()
        TimerCard(startedAt = session.scheduledAt ?: Date())
        SessionInfoCard(session = session)
        NotesCard(
            notes = notes,
            onNotesChange = onNotesChange
        )
        Button(
            onClick = onEndSession,
            enabled = !ending,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            if (ending) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.active_session_end),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        FilledTonalButton(
            onClick = onBackHome,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text(stringResource(R.string.active_session_back_home))
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun PulsingStatusBadge() {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(OnlineStatusColor)
                .alpha(alpha)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(R.string.active_session_status),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = OnlineStatusColor
        )
    }
}

@Composable
private fun TimerCard(startedAt: Date) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Text(
                text = stringResource(R.string.active_session_elapsed),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.height(4.dp))
            SessionTimer(startedAt = startedAt)
        }
    }
}

@Composable
private fun SessionInfoCard(session: Session) {
    ElevatedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AvatarCircle(initials = "ML", size = 56.dp)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = stringResource(R.string.active_session_class, session.title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = session.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            InfoRow(
                icon = Icons.Default.Schedule,
                label = stringResource(
                    R.string.active_session_time_range,
                    "15:00",
                    "16:00"
                )
            )
            Spacer(Modifier.height(8.dp))
            InfoRow(
                icon = Icons.Default.LocationOn,
                label = stringResource(R.string.active_session_location)
            )
        }
    }
}

@Composable
private fun NotesCard(notes: String, onNotesChange: (String) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.active_session_notes),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        DexterTextField(
            value = notes,
            onValueChange = onNotesChange,
            label = stringResource(R.string.active_session_notes),
            placeholder = stringResource(R.string.active_session_notes_placeholder),
            singleLine = false,
            minLines = 4,
            maxLength = 1000,
            contentDescriptionLabel = stringResource(R.string.cd_notes_input)
        )
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
private fun ActiveSessionSuccessPreview() {
    val sampleSession = Session(
        id = 1L,
        matchId = 1L,
        skillId = 1L,
        sessionNumber = 3,
        title = "Guitarra",
        description = "Acordes básicos",
        status = SessionStatus.SCHEDULED,
        scheduledAt = Date(),
        completedAt = null,
        createdAt = Date()
    )
    AppTheme(dynamicColor = false) {
        ActiveSessionContent(
            state = ActiveSessionState.Active(
                session = sampleSession,
                notes = "Revisar progresión Am-Em-Dm",
                ending = false
            ),
            onBackClick = {},
            onNotesChange = {},
            onEndSession = {},
            onBackHome = {}
        )
    }
}
