package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.Notification
import com.dexter.app.domain.model.NotificationType
import com.dexter.app.presentation.session.CurrentUserSession
import com.dexter.app.presentation.ui.components.NotificationItem
import com.dexter.app.presentation.ui.components.TopAppBarDexter
import com.dexter.app.presentation.viewmodel.NotificationsState
import com.dexter.app.presentation.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    onBackClick: () -> Unit,
    onNotifClick: (Notification) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val userId = CurrentUserSession.userId ?: 1L

    LaunchedEffect(userId) {
        viewModel.loadNotifications(userId)
    }

    NotificationsContent(
        state = state,
        onBackClick = onBackClick,
        onMarkAllRead = { viewModel.onMarkAllRead(userId) },
        onNotifClick = onNotifClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationsContent(
    state: NotificationsState,
    onBackClick: () -> Unit,
    onMarkAllRead: () -> Unit,
    onNotifClick: (Notification) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBarDexter(
                title = stringResource(R.string.notifs_title),
                onBack = onBackClick,
                actions = {
                    if (state is NotificationsState.Success && state.unread.isNotEmpty()) {
                        TextButton(onClick = onMarkAllRead) {
                            Text(stringResource(R.string.notifs_read_all))
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        when (state) {
            is NotificationsState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is NotificationsState.Empty -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.notifs_title),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is NotificationsState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            }
            is NotificationsState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    if (state.unread.isNotEmpty()) {
                        item {
                            SectionHeader(labelRes = R.string.notifs_section_new)
                        }
                        items(state.unread, key = { it.id }) { notif ->
                            NotificationItem(
                                notif = notif,
                                onClick = { onNotifClick(notif) }
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                    if (state.read.isNotEmpty()) {
                        item {
                            SectionHeader(labelRes = R.string.notifs_section_old)
                        }
                        items(state.read, key = { it.id }) { notif ->
                            NotificationItem(
                                notif = notif,
                                onClick = { onNotifClick(notif) }
                            )
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(labelRes: Int) {
    Text(
        text = stringResource(labelRes),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

/**
 * Maps a notification type to the appropriate route resolver.
 * Kept out of the ViewModel because navigation belongs to the UI layer.
 */
fun resolveNotificationRoute(notif: Notification): NotificationRoute? {
    val ref = notif.referenceId ?: return null
    return when (notif.notificationType) {
        NotificationType.NEW_MATCH -> NotificationRoute.MatchDetail(ref)
        NotificationType.SESSION_REMINDER -> NotificationRoute.ActiveSession(ref)
        NotificationType.NEW_REVIEW -> NotificationRoute.UserProfile(ref)
        NotificationType.AI_SUGGESTION -> NotificationRoute.Home
        NotificationType.NEW_MESSAGE -> null // Chat excluded in this phase
    }
}

sealed class NotificationRoute {
    data class MatchDetail(val matchId: Long) : NotificationRoute()
    data class ActiveSession(val sessionId: Long) : NotificationRoute()
    data class UserProfile(val userId: Long) : NotificationRoute()
    data object Home : NotificationRoute()
}
