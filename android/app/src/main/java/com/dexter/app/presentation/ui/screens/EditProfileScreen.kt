package com.dexter.app.presentation.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dexter.app.R
import com.dexter.app.domain.model.ProfileSummary
import com.dexter.app.presentation.ui.components.AvatarCircle
import com.dexter.app.presentation.ui.components.DexterTextField
import com.dexter.app.presentation.ui.components.SkillChip
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.EditProfileState
import com.dexter.app.presentation.viewmodel.EditProfileViewModel

/**
 * Edit Profile screen — Stateful composable.
 *
 * Receives the current [ProfileSummary] to pre-fill the form fields.
 * On save, sends PUT /profile-summary via the ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    profile: ProfileSummary,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val fullName by viewModel.fullName.collectAsState()
    val bio by viewModel.bio.collectAsState()
    val country by viewModel.country.collectAsState()
    val skillsOffered by viewModel.skillsOffered.collectAsState()

    val isSaving = state is EditProfileState.Loading
    val errorMessage = (state as? EditProfileState.Error)?.message

    LaunchedEffect(Unit) {
        viewModel.loadFromProfile(profile)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.edit_profile_title),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = { viewModel.saveProfile(onSaved = onSaved) },
                        enabled = !isSaving
                    ) {
                        Text(
                            text = stringResource(R.string.edit_profile_save_top),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        EditProfileForm(
            fullName = fullName,
            bio = bio,
            country = country,
            skillsOffered = skillsOffered,
            avatarUrl = profile.avatarUrl,
            initials = profile.initials,
            isSaving = isSaving,
            errorMessage = errorMessage,
            onFullNameChange = viewModel::onFullNameChange,
            onBioChange = viewModel::onBioChange,
            onCountryChange = viewModel::onCountryChange,
            onToggleSkill = viewModel::toggleSkillOffered,
            onSaveClick = { viewModel.saveProfile(onSaved = onSaved) },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Edit Profile form — Stateless composable.
 * Follows the same Stateful/Stateless pattern as HomeScreen/ProfileScreen.
 */
@Composable
private fun EditProfileForm(
    fullName: String,
    bio: String,
    country: String,
    skillsOffered: List<String>,
    avatarUrl: String?,
    initials: String,
    isSaving: Boolean,
    errorMessage: String?,
    onFullNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onToggleSkill: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val availableSkills = listOf(
        stringResource(R.string.skill_english),
        stringResource(R.string.skill_guitar),
        stringResource(R.string.skill_programming),
        stringResource(R.string.skill_marketing),
        stringResource(R.string.skill_photography),
        stringResource(R.string.skill_cooking),
        stringResource(R.string.skill_graphic_design)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // ── Avatar ──────────────────────────────────────────
        AvatarCircle(
            initials = initials,
            imageUrl = avatarUrl,
            size = 96.dp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Basic info section ──────────────────────────────
        Text(
            text = stringResource(R.string.edit_profile_basic_info),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        DexterTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            label = stringResource(R.string.edit_profile_name_label),
            placeholder = stringResource(R.string.onboard_name_placeholder),
            contentDescriptionLabel = stringResource(R.string.edit_profile_name_label),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        DexterTextField(
            value = country,
            onValueChange = onCountryChange,
            label = stringResource(R.string.onboard_country_label),
            placeholder = stringResource(R.string.onboard_country_placeholder),
            contentDescriptionLabel = stringResource(R.string.onboard_country_label),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        DexterTextField(
            value = bio,
            onValueChange = onBioChange,
            label = stringResource(R.string.edit_profile_about_label),
            placeholder = stringResource(R.string.profile_bio_default),
            singleLine = false,
            minLines = 3,
            contentDescriptionLabel = stringResource(R.string.edit_profile_about_label),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Skills section ──────────────────────────────────
        Text(
            text = stringResource(R.string.edit_profile_skills_section),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Chips in rows of 3 (same pattern as OnboardingScreen)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            availableSkills.chunked(3).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { skill ->
                        SkillChip(
                            label = skill,
                            selected = skill in skillsOffered,
                            isError = false,
                            onClick = { onToggleSkill(skill) }
                        )
                    }
                }
            }
        }

        // ── Error message ───────────────────────────────────
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Save button ─────────────────────────────────────
        Button(
            onClick = onSaveClick,
            enabled = !isSaving,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(R.string.edit_profile_save_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ── Previews ────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditProfilePreview() {
    AppTheme(dynamicColor = false) {
        EditProfileForm(
            fullName = "Alice Gonzalez",
            bio = "Guitar teacher",
            country = "CR",
            skillsOffered = listOf("Inglés", "Guitarra"),
            avatarUrl = null,
            initials = "AG",
            isSaving = false,
            errorMessage = null,
            onFullNameChange = {},
            onBioChange = {},
            onCountryChange = {},
            onToggleSkill = {},
            onSaveClick = {}
        )
    }
}
