package com.dexter.app.presentation.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.dexter.app.presentation.viewmodel.SettingsViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.presentation.ui.components.AvatarCircle
import com.dexter.app.presentation.ui.theme.AppTheme

@Composable
fun SettingsScreen(
    userName: String,
    userInitials: String,
    userAvatarUrl: String?,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var showConfirm by remember { mutableStateOf(false) }
    val loggedOut by viewModel.loggedOut.collectAsState()

    LaunchedEffect(loggedOut) {
        if (loggedOut) onLoggedOut()
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text(stringResource(R.string.settings_logout_confirm_title)) },
            text = { Text(stringResource(R.string.settings_logout_confirm_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirm = false
                        viewModel.logout()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.settings_logout_confirm_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirm = false }) {
                    Text(stringResource(R.string.settings_logout_confirm_no))
                }
            }
        )
    }

    SettingsContent(
        userName = userName,
        userInitials = userInitials,
        userAvatarUrl = userAvatarUrl,
        isDarkMode = isDarkMode,
        onDarkModeToggle = onDarkModeToggle,
        onBackClick = onBackClick,
        onEditProfileClick = onEditProfileClick,
        onLogoutClick = { showConfirm = true },
        modifier = modifier
    )
}

@Composable
private fun SettingsContent(
    userName: String,
    userInitials: String,
    userAvatarUrl: String?,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var matchesNotif by remember { mutableStateOf(true) }
    var remindersNotif by remember { mutableStateOf(true) }
    var ratingsNotif by remember { mutableStateOf(false) }
    var profileVisible by remember { mutableStateOf(true) }
    var shareAvailability by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back)
                )
            }
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.titleLarge
            )
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AvatarCircle(initials = userInitials, size = 54.dp, imageUrl = userAvatarUrl)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onEditProfileClick) {
                        Text(text = stringResource(R.string.settings_edit_profile))
                    }
                }
            }

            SectionLabel(label = R.string.settings_section_notifications)
            OutlinedCard(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingsToggleItem(label = R.string.settings_notif_matches, checked = matchesNotif, onCheckedChange = { matchesNotif = it })
                    SettingsDivider()
                    SettingsToggleItem(label = R.string.settings_notif_reminders, checked = remindersNotif, onCheckedChange = { remindersNotif = it })
                    SettingsDivider()
                    SettingsToggleItem(label = R.string.settings_notif_ratings, checked = ratingsNotif, onCheckedChange = { ratingsNotif = it })
                }
            }

            SectionLabel(label = R.string.settings_section_privacy)
            OutlinedCard(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingsToggleItem(label = R.string.settings_privacy_visible, checked = profileVisible, onCheckedChange = { profileVisible = it })
                    SettingsDivider()
                    SettingsToggleItem(label = R.string.settings_privacy_availability, checked = shareAvailability, onCheckedChange = { shareAvailability = it })
                }
            }

            SectionLabel(label = R.string.settings_section_appearance)
            OutlinedCard(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                SettingsToggleItem(label = R.string.settings_dark_mode, checked = isDarkMode, onCheckedChange = onDarkModeToggle)
            }

            SectionLabel(label = R.string.settings_section_account)
            OutlinedCard(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column {
                    SettingsClickableItem(label = R.string.settings_change_password, onClick = { })
                    SettingsDivider()
                    SettingsClickableItem(label = R.string.settings_language, onClick = { })
                    SettingsDivider()
                    SettingsClickableItem(R.string.settings_logout, isDanger = true, onClick = onLogoutClick)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionLabel(@StringRes label: Int, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(label),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(top = 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsToggleItem(
    @StringRes label: Int,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = stringResource(label), style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SettingsClickableItem(
    @StringRes label: Int,
    isDanger: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isDanger) FontWeight.Medium else FontWeight.Normal
        )
        if (!isDanger) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SettingsDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    AppTheme(dynamicColor = false) {
        SettingsContent("Alice Gonzalez", "AG", null, false, {}, {}, {}, {})
    }
}
