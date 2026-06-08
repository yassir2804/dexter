package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.Notification
import com.dexter.app.domain.model.NotificationType
import com.dexter.app.presentation.ui.theme.AppTheme
import java.util.Date

/**
 * Row rendering one notification with a type-coloured icon, title, body,
 * relative timestamp, and an unread dot when [Notification.isRead] is false.
 */
@Composable
fun NotificationItem(
    notif: Notification,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = iconFor(notif.notificationType)
    val tint = tintFor(notif.notificationType)
    val bg = containerFor(notif.notificationType)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .alpha(if (notif.isRead) 0.7f else 1f)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = notif.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = notif.body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatRelative(notif.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (!notif.isRead) {
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@Composable
private fun iconFor(type: NotificationType): ImageVector = when (type) {
    NotificationType.NEW_MATCH -> Icons.Default.Group
    NotificationType.SESSION_REMINDER -> Icons.Default.Schedule
    NotificationType.NEW_REVIEW -> Icons.Default.Star
    NotificationType.AI_SUGGESTION -> Icons.Default.AutoAwesome
}

@Composable
private fun tintFor(type: NotificationType): Color = when (type) {
    NotificationType.NEW_MATCH -> MaterialTheme.colorScheme.onPrimaryContainer
    NotificationType.SESSION_REMINDER -> MaterialTheme.colorScheme.onTertiaryContainer
    NotificationType.NEW_REVIEW -> MaterialTheme.colorScheme.onSurfaceVariant
    NotificationType.AI_SUGGESTION -> MaterialTheme.colorScheme.onPrimaryContainer
}

@Composable
private fun containerFor(type: NotificationType): Color = when (type) {
    NotificationType.NEW_MATCH -> MaterialTheme.colorScheme.primaryContainer
    NotificationType.SESSION_REMINDER -> MaterialTheme.colorScheme.tertiaryContainer
    NotificationType.NEW_REVIEW -> MaterialTheme.colorScheme.surfaceVariant
    NotificationType.AI_SUGGESTION -> MaterialTheme.colorScheme.primaryContainer
}

@Composable
private fun formatRelative(date: Date): String {
    val diffMs = System.currentTimeMillis() - date.time
    val minutes = diffMs / 60_000L
    val hours = minutes / 60L
    val days = hours / 24L
    return when {
        minutes < 1 -> stringResource(R.string.time_just_now)
        minutes < 60 -> stringResource(R.string.time_minutes_ago, minutes.toInt())
        hours < 24 -> if (hours == 1L) {
            stringResource(R.string.time_hours_ago, 1)
        } else {
            stringResource(R.string.time_hours_ago_plural, hours.toInt())
        }
        days == 1L -> stringResource(R.string.time_yesterday)
        else -> stringResource(R.string.time_days_ago, days.toInt())
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationItemPreview() {
    AppTheme(dynamicColor = false) {
        NotificationItem(
            notif = Notification(
                id = 1, userId = 1, referenceId = 1,
                title = "Nuevo match",
                body = "Maria Lopez quiere intercambiar habilidades contigo",
                notificationType = NotificationType.NEW_MATCH,
                isRead = false,
                createdAt = Date(System.currentTimeMillis() - 15 * 60_000L)
            ),
            onClick = {}
        )
    }
}
