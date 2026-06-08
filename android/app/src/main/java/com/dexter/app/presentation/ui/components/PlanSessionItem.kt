package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.Session
import com.dexter.app.domain.model.SessionStatus
import com.dexter.app.presentation.ui.theme.AppTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Row rendering one [session] inside the PlanScreen roadmap.
 *
 * Visual states:
 * - Completed: filled circle with check, opacity dimmed.
 * - Next (isNext): primary border + "PRÓXIMO" badge.
 * - Scheduled (default): outlined circle with session number.
 */
@Composable
fun PlanSessionItem(
    session: Session,
    isNext: Boolean,
    onEdit: () -> Unit,
    onComplete: () -> Unit = {},
    readOnly: Boolean = false,
    modifier: Modifier = Modifier
) {
    val completed = session.status == SessionStatus.COMPLETED
    val borderColor = when {
        isNext -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val containerColor = when {
        isNext -> MaterialTheme.colorScheme.primaryContainer
        completed -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth(),
        border = androidx.compose.foundation.BorderStroke(
            if (isNext) 2.dp else 1.dp,
            borderColor
        ),
        colors = androidx.compose.material3.CardDefaults.outlinedCardColors(
            containerColor = containerColor
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            SessionBadge(
                number = session.sessionNumber,
                completed = completed,
                isNext = isNext
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                if (isNext) {
                    NextBadge()
                    Spacer(Modifier.height(4.dp))
                }
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (completed) MaterialTheme.colorScheme.onSurfaceVariant
                    else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = session.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = formatDate(session.scheduledAt ?: session.completedAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (!completed && !readOnly) {
                if (isNext) {
                    IconButton(onClick = onComplete) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = stringResource(R.string.plan_complete_session),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = if (isNext) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun SessionBadge(number: Int, completed: Boolean, isNext: Boolean) {
    val bg = when {
        completed -> MaterialTheme.colorScheme.primary
        isNext -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val fg = when {
        completed || isNext -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(bg)
    ) {
        if (completed) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(18.dp)
            )
        } else {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = fg
            )
        }
    }
}

@Composable
private fun NextBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = stringResource(R.string.plan_next_badge),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

private fun formatDate(date: Date?): String {
    if (date == null) return "Sin programar"
    val fmt = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())
    return fmt.format(date)
}

@Preview(showBackground = true)
@Composable
private fun PlanSessionCompletedPreview() {
    AppTheme(dynamicColor = false) {
        PlanSessionItem(
            session = Session(
                id = 1, matchId = 1, skillId = 1, sessionNumber = 1,
                title = "Acordes básicos",
                description = "Aprender Em, Am, C, G",
                status = SessionStatus.COMPLETED,
                scheduledAt = Date(), completedAt = Date(), createdAt = Date()
            ),
            isNext = false,
            onEdit = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlanSessionNextPreview() {
    AppTheme(dynamicColor = false) {
        PlanSessionItem(
            session = Session(
                id = 3, matchId = 1, skillId = 1, sessionNumber = 3,
                title = "Primera canción",
                description = "Aplicar acordes en canción simple",
                status = SessionStatus.SCHEDULED,
                scheduledAt = Date(), completedAt = null, createdAt = Date()
            ),
            isNext = true,
            onEdit = {}
        )
    }
}
