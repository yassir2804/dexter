package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    isCreateMode: Boolean = false,
    createMatchId: Long = 0L,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(sessionId, isCreateMode) {
        if (isCreateMode) viewModel.initCreateMode(createMatchId)
        else viewModel.loadSession(sessionId)
    }

    LaunchedEffect(state) {
        if (state is EditSessionState.Saved || state is EditSessionState.Cancelled) {
            onDoneClick()
        }
    }

    EditSessionContent(
        state = state,
        isCreateMode = isCreateMode,
        onBackClick = onBackClick,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onDateSelected = viewModel::onDateSelected,
        onTimeSelected = viewModel::onTimeSelected,
        onDurationChange = viewModel::onDurationChange,
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
    isCreateMode: Boolean = false,
    onBackClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateSelected: (Int, Int, Int) -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onDurationChange: (String) -> Unit,
    onSave: () -> Unit,
    onRequestDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = if (isCreateMode) {
        stringResource(R.string.create_session_title)
    } else {
        (state as? EditSessionState.Editing)?.session?.sessionNumber?.let {
            stringResource(R.string.edit_session_title, it)
        } ?: stringResource(R.string.edit_session_confirm_title)
    }

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
                    isCreateMode = isCreateMode,
                    onTitleChange = onTitleChange,
                    onDescriptionChange = onDescriptionChange,
                    onDateSelected = onDateSelected,
                    onTimeSelected = onTimeSelected,
                    onDurationChange = onDurationChange,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditSessionForm(
    state: EditSessionState.Editing,
    isCreateMode: Boolean = false,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateSelected: (Int, Int, Int) -> Unit,
    onTimeSelected: (Int, Int) -> Unit,
    onDurationChange: (String) -> Unit,
    onSave: () -> Unit,
    onRequestDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Pre-parse fecha y hora actuales para el picker
    val currentDate = remember(state.date) {
        runCatching {
            val parts = state.date.split("/")
            Triple(parts[2].toInt(), parts[1].toInt() - 1, parts[0].toInt())
        }.getOrNull()
    }
    val currentTime = remember(state.time) {
        runCatching {
            val parts = state.time.split(":")
            Pair(parts[0].toInt(), parts[1].toInt())
        }.getOrNull()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = if (currentDate != null) {
            java.util.Calendar.getInstance().apply {
                set(currentDate.first, currentDate.second, currentDate.third, 0, 0, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis
        } else null
    )

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime?.first ?: 9,
        initialMinute = currentTime?.second ?: 0,
        is24Hour = true
    )

    // DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = java.util.Calendar.getInstance().apply { timeInMillis = millis }
                        onDateSelected(
                            cal.get(java.util.Calendar.YEAR),
                            cal.get(java.util.Calendar.MONTH),
                            cal.get(java.util.Calendar.DAY_OF_MONTH)
                        )
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // TimePickerDialog
    if (showTimePicker) {
        Dialog(onDismissRequest = { showTimePicker = false }) {
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.edit_session_field_time),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    TimePicker(state = timePickerState)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
                        TextButton(onClick = {
                            onTimeSelected(timePickerState.hour, timePickerState.minute)
                            showTimePicker = false
                        }) { Text("OK") }
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Título — singleLine=false para poder mover el cursor al final
        DexterTextField(
            value = state.title,
            onValueChange = onTitleChange,
            label = stringResource(R.string.edit_session_field_title),
            placeholder = stringResource(R.string.edit_session_field_title_placeholder),
            leadingIcon = Icons.Default.Title,
            singleLine = false,
            minLines = 1,
            maxLength = 100,
            contentDescriptionLabel = stringResource(R.string.edit_session_field_title)
        )
        DexterTextField(
            value = state.description,
            onValueChange = onDescriptionChange,
            label = stringResource(R.string.edit_session_field_description),
            placeholder = stringResource(R.string.edit_session_field_description_placeholder),
            singleLine = false,
            minLines = 3,
            maxLength = 500,
            contentDescriptionLabel = stringResource(R.string.edit_session_field_description)
        )

        // Fecha — readonly, abre DatePicker al tocar
        OutlinedTextField(
            value = state.date.ifBlank { "" },
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.edit_session_field_date)) },
            placeholder = { Text("DD/MM/YYYY") },
            leadingIcon = {
                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(20.dp))
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            enabled = false,
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.outline,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            shape = RoundedCornerShape(4.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Hora — readonly, abre TimePicker al tocar
            Box(Modifier.weight(1f)) {
                OutlinedTextField(
                    value = state.time.ifBlank { "" },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.edit_session_field_time)) },
                    placeholder = { Text("HH:MM") },
                    leadingIcon = {
                        Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(20.dp))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTimePicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                    shape = RoundedCornerShape(4.dp)
                )
            }
            // Duración — libre pero solo números
            Box(Modifier.weight(1f)) {
                DexterTextField(
                    value = state.duration,
                    onValueChange = onDurationChange,
                    label = stringResource(R.string.edit_session_field_duration),
                    keyboardType = KeyboardType.Number,
                    maxLength = 3,
                    isError = state.durationError != null,
                    supportingText = state.durationError,
                    contentDescriptionLabel = stringResource(R.string.edit_session_field_duration)
                )
            }
        }

        // Error general (balance de skills, fecha duplicada, etc.)
        if (state.generalError != null) {
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.errorContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = state.generalError,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onSave,
            enabled = !state.saving,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(50)
        ) {
            if (state.saving) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
            } else {
                Text(
                    text = stringResource(if (isCreateMode) R.string.create_session_save else R.string.edit_session_save),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        if (!isCreateMode) {
            OutlinedButton(
                onClick = onRequestDelete,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text(stringResource(R.string.edit_session_delete))
            }
        }

        Spacer(Modifier.height(24.dp))
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
            onDateSelected = { _, _, _ -> },
            onTimeSelected = { _, _ -> },
            onDurationChange = {},
            onSave = {},
            onRequestDelete = {},
            onConfirmDelete = {},
            onDismissDelete = {}
        )
    }
}
