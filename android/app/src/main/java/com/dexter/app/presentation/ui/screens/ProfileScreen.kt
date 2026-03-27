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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.Review
import com.dexter.app.domain.model.Skill
import com.dexter.app.domain.model.SkillLevel
import com.dexter.app.domain.model.SkillRole
import com.dexter.app.domain.model.User
import com.dexter.app.presentation.session.CurrentUserSession
import com.dexter.app.presentation.ui.components.AvatarCircle
import com.dexter.app.presentation.ui.components.ReviewCard
import com.dexter.app.presentation.ui.components.SkillChip
import com.dexter.app.presentation.ui.components.StatCard
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.ui.theme.StarColor
import com.dexter.app.presentation.viewmodel.ProfileState
import com.dexter.app.presentation.viewmodel.ProfileViewModel
import com.dexter.app.presentation.viewmodel.ReviewWithReviewer
import java.util.Date

/**
 * Profile screen — Stateful composable.
 *
 * Receives navigation callbacks as lambdas (NOT the NavController).
 */
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profileState by profileViewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        CurrentUserSession.userId?.let { profileViewModel.loadProfile(it) }
    }

    ProfileContent(
        profileState = profileState,
        onEditProfileClick = onEditProfileClick,
        modifier = modifier
    )
}

/**
 * Profile content — Stateless composable.
 */
@Composable
private fun ProfileContent(
    profileState: ProfileState,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        when (profileState) {
            is ProfileState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is ProfileState.Success -> {
                ProfileSuccessContent(
                    user = profileState.user,
                    skillsOffered = profileState.skillsOffered,
                    skillsSought = profileState.skillsSought,
                    reviews = profileState.reviews,
                    onEditProfileClick = onEditProfileClick
                )
            }

            is ProfileState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.profile_bio_default),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is ProfileState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = profileState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileSuccessContent(
    user: User,
    skillsOffered: List<Skill>,
    skillsSought: List<Skill>,
    reviews: List<ReviewWithReviewer>,
    onEditProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val initials = "${user.firstName.firstOrNull() ?: ""}${user.lastName.firstOrNull() ?: ""}"

    Column(modifier = modifier) {
        // ── Profile Header ──────────────────────────────────────────
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                // Avatar with camera badge
                Box {
                    AvatarCircle(
                        initials = initials,
                        size = 80.dp,
                        imageUrl = user.avatarUrl
                    )
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 2.dp, y = 2.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = stringResource(R.string.cd_camera),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${user.firstName} ${user.lastName}",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = user.bio ?: stringResource(R.string.profile_bio_default),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!user.country.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = countryCodeToFlag(user.country),
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Stats Row ───────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                value = user.totalMatches.toString(),
                label = R.string.profile_stat_matches,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = user.totalSessions.toString(),
                label = R.string.profile_stat_sessions,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = user.rating.toString(),
                label = R.string.profile_stat_rating,
                valueColor = StarColor,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Skills Offered ──────────────────────────────────────────
        if (skillsOffered.isNotEmpty()) {
            Text(
                text = stringResource(R.string.profile_skills_offered),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedCard(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    skillsOffered.forEach { skill ->
                        SkillChip(label = skill.name, selected = true)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Skills Wanted ───────────────────────────────────────────
        if (skillsSought.isNotEmpty()) {
            Text(
                text = stringResource(R.string.profile_skills_wanted),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedCard(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    skillsSought.forEach { skill ->
                        SkillChip(label = skill.name, isError = true)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Reviews ─────────────────────────────────────────────────
        if (reviews.isNotEmpty()) {
            Text(
                text = stringResource(R.string.profile_reviews),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            reviews.forEach { item ->
                ReviewCard(
                    reviewerName = item.reviewerName,
                    reviewerImageUrl = item.reviewerAvatarUrl,
                    rating = item.review.rating,
                    comment = item.review.comment ?: ""
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ── Edit Profile Button ─────────────────────────────────────
        OutlinedButton(
            onClick = onEditProfileClick,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text(
                text = stringResource(R.string.profile_edit),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ── Helpers ─────────────────────────────────────────────────────────

private fun countryCodeToFlag(countryCode: String): String {
    val code = countryCode.uppercase()
    if (code.length != 2) return countryCode
    val first = Character.toChars(0x1F1E6 - 'A'.code + code[0].code)
    val second = Character.toChars(0x1F1E6 - 'A'.code + code[1].code)
    return String(first) + String(second)
}

// ── Previews ────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenPreview() {
    AppTheme(dynamicColor = false) {
        ProfileContent(
            profileState = ProfileState.Success(
                user = User(
                    id = 1, email = "alice@example.com", password = null,
                    firstName = "Alice", lastName = "Gonzalez", username = "alice",
                    bio = "Guitar teacher", avatarUrl = null, country = "CR",
                    enabled = true, tokenExpired = false, rating = 4.8,
                    totalSessions = 12, totalMatches = 5,
                    createDate = Date(), updatedAt = Date()
                ),
                skillsOffered = listOf(
                    Skill(1, 1, "Guitarra", "Musica", SkillRole.OFFERED, SkillLevel.ADVANCED),
                    Skill(3, 1, "Ingles", "Idiomas", SkillRole.OFFERED, SkillLevel.INTERMEDIATE),
                    Skill(4, 1, "Marketing", "Negocios", SkillRole.OFFERED, SkillLevel.INTERMEDIATE)
                ),
                skillsSought = listOf(
                    Skill(5, 1, "Fotografia", "Arte", SkillRole.SOUGHT, SkillLevel.BEGINNER),
                    Skill(6, 1, "Piano", "Musica", SkillRole.SOUGHT, SkillLevel.BEGINNER)
                ),
                reviews = listOf(
                    ReviewWithReviewer(
                        review = Review(1, 2, 1, 1, 5, "Excelente profesora de guitarra.", Date()),
                        reviewerName = "Bob Smith"
                    ),
                    ReviewWithReviewer(
                        review = Review(2, 2, 1, 2, 4, "Buenas sesiones de ingles.", Date()),
                        reviewerName = "Bob Smith"
                    )
                )
            ),
            onEditProfileClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ProfileScreenLoadingPreview() {
    AppTheme(dynamicColor = false) {
        ProfileContent(
            profileState = ProfileState.Loading,
            onEditProfileClick = {}
        )
    }
}
