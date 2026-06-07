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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.Session
import com.dexter.app.domain.model.SessionStatus
import com.dexter.app.presentation.ui.components.DexterTextField
import com.dexter.app.presentation.ui.components.TopAppBarDexter
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.EditSessionState
import com.dexter.app.presentation.viewmodel.EditSessionViewModel
import java.util.Date

@Composable
fun EditSessionScreen(
    sessionId: Long,
    viewModel: EditSessionViewModel,
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.loadSession(sessionId)
    }

    LaunchedEffect(state) {
        if (state is EditSessionState.Saved || state is EditSessionState.Cancelled) {
            onDoneClick()
        }
    }

    EditSessionContent(
        state = state,
        onBackClick = onBackClick,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onDateChange = viewModel::onDateChange,
        onTimeChange = viewModel::onTimeChange,
        onDurationChange = viewModel::onDurationChange,
        onSlotSelected = viewModel::onSlotSelected,
        onSave = viewModel::onSave,
        onRequestDelete = viewModel::onRequestDelete,
        onConfirmDelete = viewModel::onConfirmDelete,
        onDismissDelete = viewModel::onDismissDelete,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSessionContent(
    state: EditSessionState,
    onBackClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onSlotSelected: (com.dexter.app.domain.model.ScheduleSlot) -> Unit = {},
    onSave: () -> Unit,
    onRequestDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = (state as? EditSessionState.Editing)?.session?.sessionNumber?.let {
        stringResource(R.string.edit_session_title, it)
    } ?: stringResource(R.string.edit_session_confirm_title)

    Scaffold(
        topBar = { TopAppBarDexter(title = title, onBack = onBackClick) },
        modifier = modifier
    ) { padding ->
        when (state) {
            is EditSessionState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is EditSessionState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is EditSessionState.Editing -> {
                EditSessionForm(
                    state = state,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange,
                    onDateChange = onDateChange,
                    onTimeChange = onTimeChange,
                    onDurationChange = onDurationChange,
                    onSlotSelected = onSlotSelected,
                    onSave = onSave,
                    onRequestDelete = onRequestDelete,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
                if (state.showDeleteConfirm) {
                    DeleteConfirmDialog(
                        onConfirm = onConfirmDelete,
                        onDismiss = onDismissDelete,
                        deleting = state.deleting
                    )
                }
            }
            EditSessionState.Saved, EditSessionState.Cancelled -> {
                // LaunchedEffect in the Screen will navigate away.
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
private fun EditSessionForm(
    state: EditSessionState.Editing,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onDurationChange: (String) -> Unit,
    onSlotSelected: (com.dexter.app.domain.model.ScheduleSlot) -> Unit = {},
    onSave: () -> Unit,
    onRequestDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DexterTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = stringResource(R.string.edit_session_field_title),
            placeholder = stringResource(R.string.edit_session_field_title_placeholder),
            leadingIcon = Icons.Default.Title,
            contentDescriptionLabel = stringResource(R.string.edit_session_field_title)
        )
        DexterTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = stringResource(R.string.edit_session_field_description),
            placeholder = stringResource(R.string.edit_session_field_description_placeholder),
            singleLine = false,
            minLines = 3,
            contentDescriptionLabel = stringResource(R.string.edit_session_field_description)
        )
        DexterTextField(
            value = state.date,
            onValueChange = onDateChange,
            label = stringResource(R.string.edit_session_field_date),
            leadingIcon = Icons.Default.CalendarToday,
            contentDescriptionLabel = stringResource(R.string.edit_session_field_date)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.weight(1f)) {
                DexterTextField(
                    value = state.time,
                    onValueChange = onTimeChange,
                    label = stringResource(R.string.edit_session_field_time),
                    leadingIcon = Icons.Default.Schedule,
                    contentDescriptionLabel = stringResource(R.string.edit_session_field_time)
                )
            }
            Box(Modifier.weight(1f)) {
                DexterTextField(
                    value = state.duration,
                    onValueChange = onDurationChange,
                    label = stringResource(R.string.edit_session_field_duration),
                    keyboardType = KeyboardType.Number,
                    contentDescriptionLabel = stringResource(R.string.edit_session_field_duration)
                )
            }
        }

        AiSuggestionCard(
            slots = state.scheduleSlots,
            loading = state.loadingSlots,
            onSlotSelected = onSlotSelected
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onSave,
            enabled = !state.saving,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50)
        ) {
            if (state.saving) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.edit_session_save),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        OutlinedButton(
            onClick = { /* back = cancel */ onRequestDelete() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text(stringResource(R.string.edit_session_delete))
        }

        Spacer(Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AiSuggestionCard(
    slots: List<com.dexter.app.domain.model.ScheduleSlot>,
    loading: Boolean,
    onSlotSelected: (com.dexter.app.domain.model.ScheduleSlot) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.edit_session_ai_suggestion),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.size(4.dp))
            Text(
                text = stringResource(R.string.edit_session_ai_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(Modifier.size(12.dp))
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterHorizontally),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else if (slots.isEmpty()) {
                Text(
                    text = "No se pudieron generar sugerencias",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    slots.forEach { slot ->
                        Surface(
                            onClick = { onSlotSelected(slot) },
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${slot.day} • ${slot.time}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    deleting: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(36.dp)
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.edit_session_confirm_heading),
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = stringResource(R.string.edit_session_confirm_description, "tu compañero")
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !deleting,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.edit_session_confirm_yes))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !deleting) {
                Text(stringResource(R.string.edit_session_confirm_no))
            }
        }
    )
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun EditSessionSuccessPreview() {
    val sampleSession = Session(
        id = 1L,
        matchId = 1L,
        skillId = 1L,
        sessionNumber = 2,
        title = "Guitarra: Acordes",
        description = "Repasar progresión Am-Em-Dm",
        status = SessionStatus.SCHEDULED,
        scheduledAt = Date(),
        completedAt = null,
        createdAt = Date()
    )
    AppTheme(dynamicColor = false) {
        EditSessionContent(
            state = EditSessionState.Editing(
                session = sampleSession,
                title = sampleSession.title,
                description = sampleSession.description,
                date = "20/04/2026",
                time = "15:00",
                duration = "60",
                showDeleteConfirm = false,
                saving = false,
                deleting = false
            ),
            onBackClick = {},
            onTitleChange = {},
            onDescriptionChange = {},
            onDateChange = {},
            onTimeChange = {},
            onDurationChange = {},
            onSave = {},
            onRequestDelete = {},
            onConfirmDelete = {},
            onDismissDelete = {}
        )
    }
}
