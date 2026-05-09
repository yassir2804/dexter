package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.ProfileReviewItem
import com.dexter.app.domain.model.ProfileSummary
import com.dexter.app.presentation.ui.components.AvatarCircle
import com.dexter.app.presentation.ui.components.PreviewSafeFlowRow
import com.dexter.app.presentation.ui.components.ReviewCard
import com.dexter.app.presentation.ui.components.SkillChip
import com.dexter.app.presentation.ui.components.StatCard
import com.dexter.app.presentation.ui.components.TopAppBarDexter
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.ui.theme.StarColor
import com.dexter.app.presentation.session.CurrentUserSession
import com.dexter.app.presentation.viewmodel.ConnectState
import com.dexter.app.presentation.viewmodel.UserProfileState
import com.dexter.app.presentation.viewmodel.UserProfileViewModel

@Composable
fun UserProfileScreen(
    userId: Long,
    viewModel: UserProfileViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val connectState by viewModel.connectState.collectAsState()

    LaunchedEffect(userId) { viewModel.loadProfile(userId) }

    UserProfileContent(
        state = state,
        connectState = connectState,
        onBackClick = onBackClick,
        onConnectClick = {
            val initiatorId = CurrentUserSession.userId ?: return@UserProfileContent
            viewModel.initiateMatch(initiatorId, userId)
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserProfileContent(
    state: UserProfileState,
    connectState: ConnectState,
    onBackClick: () -> Unit,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBarDexter(
                title = stringResource(R.string.user_profile_title),
                onBack = onBackClick
            )
        },
        modifier = modifier
    ) { padding ->
        when (state) {
            is UserProfileState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is UserProfileState.Empty -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.profile_bio_default),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is UserProfileState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { Text(state.message, color = MaterialTheme.colorScheme.error) }
            }
            is UserProfileState.Success -> {
                UserProfileBody(
                    profile = state.profile,
                    connectState = connectState,
                    onConnectClick = onConnectClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UserProfileBody(
    profile: ProfileSummary,
    connectState: ConnectState,
    onConnectClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ── Header ──────────────────────────────────────────────────
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                AvatarCircle(
                    initials = profile.initials,
                    imageUrl = profile.avatarUrl,
                    size = 96.dp
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "${profile.firstName} ${profile.lastName}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
                if (!profile.country.isNullOrBlank()) {
                    Text(
                        text = profile.country,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Stats row ───────────────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                value = profile.totalSessions.toString(),
                label = R.string.user_profile_sessions,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = profile.rating.toString(),
                label = R.string.user_profile_rating,
                valueColor = StarColor,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = "${profile.totalMatches}",
                label = R.string.user_profile_match,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        // ── Action buttons ──────────────────────────────────────────
        val isConnecting = connectState is ConnectState.Loading
        val connectDone = connectState is ConnectState.Done
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { /* Chat excluded in this phase */ },
                enabled = false,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text(stringResource(R.string.user_profile_message))
            }
            FilledTonalButton(
                onClick = onConnectClick,
                enabled = !isConnecting && !connectDone,
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp),
                shape = RoundedCornerShape(50)
            ) {
                Text(stringResource(R.string.user_profile_schedule))
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── About ───────────────────────────────────────────────────
        if (profile.bio.isNotBlank()) {
            Text(
                text = stringResource(R.string.user_profile_about),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            OutlinedCard(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = profile.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        // ── Skills offered ──────────────────────────────────────────
        if (profile.skillsOffered.isNotEmpty()) {
            Text(
                text = stringResource(R.string.user_profile_skills_offered),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            PreviewSafeFlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                profile.skillsOffered.forEach { SkillChip(label = it, selected = true) }
            }
            Spacer(Modifier.height(16.dp))
        }

        // ── Skills wanted ───────────────────────────────────────────
        if (profile.skillsSought.isNotEmpty()) {
            Text(
                text = stringResource(R.string.user_profile_skills_wanted),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            PreviewSafeFlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                profile.skillsSought.forEach { SkillChip(label = it, isError = true) }
            }
            Spacer(Modifier.height(16.dp))
        }

        // ── Reviews ─────────────────────────────────────────────────
        if (profile.reviews.isNotEmpty()) {
            Text(
                text = stringResource(R.string.user_profile_reviews, profile.reviews.size),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            profile.reviews.forEach { review ->
                ReviewCard(
                    reviewerName = review.reviewerName,
                    reviewerImageUrl = review.reviewerAvatarUrl,
                    rating = review.rating,
                    comment = review.comment
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true, heightDp = 1800)
@Composable
private fun UserProfileSuccessPreview() {
    val sampleProfile = ProfileSummary(
        initials = "ML",
        firstName = "Maria",
        lastName = "Lopez",
        avatarUrl = null,
        bio = "Guitarrista apasionada con 10 años de experiencia. Busco mejorar mi inglés para estudios en el extranjero.",
        country = "Costa Rica",
        rating = 4.8,
        totalSessions = 23,
        totalMatches = 15,
        skillsOffered = listOf("Guitarra", "Ukulele", "Solfeo"),
        skillsSought = listOf("Inglés", "Francés"),
        reviews = listOf(
            ProfileReviewItem(
                reviewerName = "Juan Pérez",
                reviewerAvatarUrl = null,
                rating = 5,
                comment = "Maria es una excelente profesora, muy paciente y clara."
            ),
            ProfileReviewItem(
                reviewerName = "Ana Gómez",
                reviewerAvatarUrl = null,
                rating = 4,
                comment = "Muy buena sesión, aprendí los acordes básicos."
            )
        )
    )
    AppTheme(dynamicColor = false) {
        UserProfileContent(
            state = UserProfileState.Success(sampleProfile),
            connectState = ConnectState.Idle,
            onBackClick = {},
            onConnectClick = {}
        )
    }
}
