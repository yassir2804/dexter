package com.dexter.app.presentation.viewmodel

import android.util.Log
import androidx.annotation.StringRes
import com.dexter.app.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dexter.app.domain.model.Session
import com.dexter.app.domain.model.SessionStatus
import com.dexter.app.domain.repository.SessionRepository
import com.dexter.app.presentation.ui.utils.InputFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed class EditSessionState {
    data object Loading : EditSessionState()
    data class Editing(
        val session: Session,
        val title: String,
        val description: String,
        val date: String,
        val time: String,
        val duration: String,
        val showDeleteConfirm: Boolean,
        val saving: Boolean,
        val deleting: Boolean,
        @StringRes val titleError: Int? = null,
        @StringRes val descriptionError: Int? = null,
        @StringRes val durationError: Int? = null,
        @StringRes val generalError: Int? = null,
        val isCreateMode: Boolean = false,
        val totalSessions: Int = 0,
        // skillIds de sesiones restantes por skill — para validar balance al borrar
        val skillCountBySkillId: Map<Long, Int> = emptyMap(),
        // fechas de otras sesiones para validar orden
        val otherSessionDates: List<Pair<Int, Date>> = emptyList(), // sessionNumber to date
    ) : EditSessionState()
    data object Saved : EditSessionState()
    data object Cancelled : EditSessionState()
    data class Error(val message: String) : EditSessionState()
}

/**
 * Edits a session's metadata (title/description/date/time/duration)
 * and supports cancelling the session with a confirmation dialog.
 */
@HiltViewModel
class EditSessionViewModel @Inject constructor(
    private val repository: SessionRepository
) : ViewModel() {

    companion object {
        private const val TAG = "EditSessionViewModel"
        private const val DEFAULT_DURATION = "60"
    }

    private val _state = MutableStateFlow<EditSessionState>(EditSessionState.Loading)
    val state = _state.asStateFlow()

    private val dateFmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

    fun loadSession(sessionId: Long) {
        viewModelScope.launch {
            _state.value = EditSessionState.Loading
            repository.getSessionById(sessionId)
                .onSuccess { session ->
                    val allSessions = repository.getSessionsByMatch(session.matchId)
                        .getOrElse { emptyList() }
                        .filter { it.status != SessionStatus.CANCELLED }
                    val skillCount = allSessions.groupBy { it.skillId }.mapValues { it.value.size }
                    val otherDates = allSessions
                        .filter { it.id != session.id && it.scheduledAt != null }
                        .map { it.sessionNumber to it.scheduledAt!! }
                    _state.value = EditSessionState.Editing(
                        session = session,
                        title = session.title,
                        description = session.description,
                        date = session.scheduledAt?.let { dateFmt.format(it) } ?: "",
                        time = session.scheduledAt?.let { timeFmt.format(it) } ?: "",
                        duration = session.duration?.toString() ?: DEFAULT_DURATION,
                        showDeleteConfirm = false,
                        saving = false,
                        deleting = false,
                        totalSessions = allSessions.size,
                        skillCountBySkillId = skillCount,
                        otherSessionDates = otherDates,
                    )
                }
                .onFailure { exception ->
                    Log.e(TAG, "Error loading session", exception)
                    _state.value = EditSessionState.Error(
                        exception.message ?: "Error desconocido"
                    )
                }
        }
    }

    fun initCreateMode(matchId: Long) {
        viewModelScope.launch {
            _state.value = EditSessionState.Loading
            val sessions = repository.getSessionsByMatch(matchId)
                .getOrElse { emptyList() }
                .filter { it.status != SessionStatus.CANCELLED }
            val nextNumber = (sessions.maxOfOrNull { it.sessionNumber } ?: 0) + 1
            val placeholder = Session(
                id = 0L,
                matchId = matchId,
                skillId = sessions.firstOrNull()?.skillId ?: 0L,
                sessionNumber = nextNumber,
                title = "",
                description = "",
                status = SessionStatus.SCHEDULED,
                scheduledAt = null,
                completedAt = null,
                createdAt = Date(),
                duration = 60
            )
            _state.value = EditSessionState.Editing(
                session = placeholder,
                title = "",
                description = "",
                date = "",
                time = "",
                duration = DEFAULT_DURATION,
                showDeleteConfirm = false,
                saving = false,
                deleting = false,
                isCreateMode = true,
                totalSessions = sessions.size,
            )
        }
    }

    private fun currentEditing(): EditSessionState.Editing? =
        _state.value as? EditSessionState.Editing

    fun onTitleChange(value: String) {
        currentEditing()?.let { _state.value = it.copy(title = InputFilters.filterSafeText(value), titleError = null) }
    }

    fun onDescriptionChange(value: String) {
        currentEditing()?.let { _state.value = it.copy(description = InputFilters.filterSafeText(value), descriptionError = null) }
    }

    fun onDateChange(value: String) {
        currentEditing()?.let { _state.value = it.copy(date = InputFilters.filterDate(value)) }
    }

    fun onTimeChange(value: String) {
        currentEditing()?.let { _state.value = it.copy(time = InputFilters.filterTime(value)) }
    }

    fun onDurationChange(value: String) {
        val filtered = InputFilters.filterDigits(value)
        currentEditing()?.let { _state.value = it.copy(duration = filtered, durationError = null) }
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        val d = String.format("%02d/%02d/%04d", day, month + 1, year)
        currentEditing()?.let { _state.value = it.copy(date = d) }
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        val t = String.format("%02d:%02d", hour, minute)
        currentEditing()?.let { _state.value = it.copy(time = t) }
    }

    fun onRequestDelete() {
        currentEditing()?.let { _state.value = it.copy(showDeleteConfirm = true) }
    }

    fun onDismissDelete() {
        currentEditing()?.let { _state.value = it.copy(showDeleteConfirm = false) }
    }

    fun onSave() {
        val editing = currentEditing() ?: return

        // Validar campos obligatorios (título y descripción). Se acumulan para
        // mostrar todos los errores de una vez, no de uno en uno.
        val titleError = if (editing.title.isBlank()) R.string.edit_session_error_title_required else null
        val descriptionError = if (editing.description.isBlank()) R.string.edit_session_error_description_required else null

        // Validar duración
        val dur = editing.duration.toIntOrNull()
        val durationError = if (dur == null || dur < 15 || dur > 480) R.string.edit_session_error_duration_range else null

        if (titleError != null || descriptionError != null || durationError != null) {
            _state.value = editing.copy(
                titleError = titleError,
                descriptionError = descriptionError,
                durationError = durationError
            )
            return
        }

        // Validar fecha
        val scheduledAt = parseScheduledAt(editing.date, editing.time)
        if (editing.date.isNotBlank() && scheduledAt != null) {
            val today = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.time
            if (scheduledAt.before(today)) {
                _state.value = editing.copy(generalError = R.string.edit_session_error_date_past)
                return
            }
            // Solo validar que no haya dos sesiones en la misma fecha exacta
            val sameDate = editing.otherSessionDates.any { (_, d) ->
                d.year == scheduledAt.year && d.month == scheduledAt.month && d.date == scheduledAt.date
            }
            if (sameDate) {
                _state.value = editing.copy(generalError = R.string.edit_session_error_date_duplicate)
                return
            }
        }

        viewModelScope.launch {
            _state.value = editing.copy(saving = true)
            val updated = editing.session.copy(
                title = editing.title,
                description = editing.description,
                scheduledAt = if (editing.date.isNotBlank()) scheduledAt else editing.session.scheduledAt,
                duration = dur!! // garantizado no-nulo: ya validado arriba
            )
            val result = if (editing.isCreateMode) repository.createSession(updated)
                else repository.updateSession(updated)
            result
                .onSuccess { _state.value = EditSessionState.Saved }
                .onFailure { exception ->
                    Log.e(TAG, "Error saving session", exception)
                    _state.value = editing.copy(saving = false)
                }
        }
    }

    private fun parseScheduledAt(date: String, time: String): Date? {
        if (date.isBlank()) return null
        val dateTime = if (time.isBlank()) date else "$date $time"
        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
        val formats = listOf(
            "dd/MM/yyyy HH:mm",
            "dd/MM/yyyy"
        )
        for (pattern in formats) {
            try {
                return SimpleDateFormat(pattern, Locale("es")).parse(dateTime)
            } catch (_: Exception) { }
        }
        val shortFormats = listOf(
            "EEE dd MMM HH:mm",
            "EEE dd MMM"
        )
        for (pattern in shortFormats) {
            try {
                val parsed = SimpleDateFormat(pattern, Locale("es")).parse(dateTime) ?: continue
                val cal = java.util.Calendar.getInstance()
                cal.time = parsed
                cal.set(java.util.Calendar.YEAR, currentYear)
                return cal.time
            } catch (_: Exception) { }
        }
        return null
    }

    fun onConfirmDelete() {
        val editing = currentEditing() ?: return

        // Mínimo 2 sesiones totales
        if (editing.totalSessions <= 2) {
            _state.value = editing.copy(
                showDeleteConfirm = false,
                generalError = R.string.edit_session_error_min_per_skill
            )
            return
        }

        // Validar balance: al borrar esta sesión, el skill de esta sesión
        // debe seguir teniendo al menos 1 sesión restante
        val mySkillId = editing.session.skillId
        val countOfMySkill = editing.skillCountBySkillId[mySkillId] ?: 1
        if (countOfMySkill <= 1) {
            _state.value = editing.copy(
                showDeleteConfirm = false,
                generalError = R.string.edit_session_error_min_per_skill
            )
            return
        }

        viewModelScope.launch {
            _state.value = editing.copy(deleting = true)
            repository.cancelSession(editing.session.id)
                .onSuccess { _state.value = EditSessionState.Cancelled }
                .onFailure { exception ->
                    Log.e(TAG, "Error cancelling session", exception)
                    _state.value = editing.copy(deleting = false, showDeleteConfirm = false)
                }
        }
    }
}
