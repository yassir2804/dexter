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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.SessionInvitation
import com.dexter.app.presentation.ui.components.AvatarCircle
import com.dexter.app.presentation.ui.components.DexterTextField
import com.dexter.app.presentation.ui.components.InfoRow
import com.dexter.app.presentation.ui.components.TopAppBarDexter
import com.dexter.app.presentation.viewmodel.SessionInviteState
import com.dexter.app.presentation.viewmodel.SessionInviteViewModel

@Composable
fun SessionInviteScreen(
    invitationId: Long,
    viewModel: SessionInviteViewModel,
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(invitationId) {
        viewModel.load(invitationId)
    }

    LaunchedEffect(state) {
        if (state is SessionInviteState.Done) onDoneClick()
    }

    SessionInviteContent(
        state = state,
        onBackClick = onBackClick,
        onAccept = viewModel::onAccept,
        onReject = viewModel::onReject,
        onTopicChange = viewModel::onTopicChange,
        onMessageChange = viewModel::onMessageChange,
        onSend = { viewModel.onSend(targetUserId = 2L) },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SessionInviteContent(
    state: SessionInviteState,
    onBackClick: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onTopicChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val titleRes = when (state) {
        is SessionInviteState.Send -> R.string.invite_send_title
        else -> R.string.invite_receive_title
    }
    Scaffold(
        topBar = {
            TopAppBarDexter(
                title = stringResource(titleRes),
                onBack = onBackClick
            )
        },
        modifier = modifier
    ) { padding ->
        when (state) {
            is SessionInviteState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is SessionInviteState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            }
            is SessionInviteState.Receive -> {
                ReceiveBody(
                    invitation = state.invitation,
                    responding = state.responding,
                    onAccept = onAccept,
                    onReject = onReject,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
            is SessionInviteState.Send -> {
                SendBody(
                    topic = state.topic,
                    message = state.message,
                    sending = state.sending,
                    onTopicChange = onTopicChange,
                    onMessageChange = onMessageChange,
                    onSend = onSend,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
            SessionInviteState.Done -> {
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
private fun ReceiveBody(
    invitation: SessionInvitation,
    responding: Boolean,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Text(
                text = stringResource(R.string.invite_badge),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
        AvatarCircle(initials = "ML", size = 80.dp)
        Text(
            text = "Maria Lopez ${stringResource(R.string.invite_user_invited)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        ElevatedCard(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.invite_details),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = stringResource(R.string.invite_in_days, 2),
                    value = "Jueves, 18 de abril"
                )
                InfoRow(
                    icon = Icons.Default.Schedule,
                    label = stringResource(R.string.invite_duration, "15:00", "60 min")
                )
                InfoRow(
                    icon = Icons.Default.LocationOn,
                    label = stringResource(R.string.invite_location_online)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.invite_topic),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.size(4.dp))
                        Text(
                            text = "Acordes básicos de guitarra",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        FilledCard(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.invite_message),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = "¡Hola! Propongo empezar con los acordes abiertos. ¿Te viene bien?",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onReject,
                enabled = !responding,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text(stringResource(R.string.invite_reject))
            }
            Button(
                onClick = onAccept,
                enabled = !responding,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (responding) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(stringResource(R.string.invite_accept))
                }
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun SendBody(
    topic: String,
    message: String,
    sending: Boolean,
    onTopicChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.invite_send_description, "Maria Lopez"),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DexterTextField(
            value = topic,
            onValueChange = onTopicChange,
            label = stringResource(R.string.invite_topic_label),
            contentDescriptionLabel = stringResource(R.string.invite_topic_label)
        )
        DexterTextField(
            value = message,
            onValueChange = onMessageChange,
            label = stringResource(R.string.invite_message_label),
            placeholder = stringResource(R.string.invite_message_placeholder),
            singleLine = false,
            minLines = 4,
            contentDescriptionLabel = stringResource(R.string.invite_message_label)
        )
        Button(
            onClick = onSend,
            enabled = topic.isNotBlank() && !sending,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50)
        ) {
            if (sending) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.invite_send_button),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
