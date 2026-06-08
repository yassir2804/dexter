package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dexter.app.R
import com.dexter.app.domain.model.ProfileSummary
import com.dexter.app.presentation.ui.components.AvatarCircle
import com.dexter.app.presentation.ui.components.DexterTextField
import com.dexter.app.presentation.ui.components.SkillChip
import com.dexter.app.presentation.ui.components.SkillPickerBottomSheet
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.EditProfileState
import com.dexter.app.presentation.viewmodel.EditProfileViewModel

// Which picker is currently open
private enum class PickerMode { OFFERED, SOUGHT }

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
    val skillsSought by viewModel.skillsSought.collectAsState()
    val customSkills by viewModel.customSkills.collectAsState()

    val isSaving = state is EditProfileState.Loading
    val errorMessage = (state as? EditProfileState.Error)?.message

    var pickerMode by remember { mutableStateOf<PickerMode?>(null) }

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
            skillsSought = skillsSought,
            avatarUrl = profile.avatarUrl,
            initials = profile.initials,
            isSaving = isSaving,
            errorMessage = errorMessage,
            onFullNameChange = viewModel::onFullNameChange,
            onBioChange = viewModel::onBioChange,
            onCountryChange = viewModel::onCountryChange,
            onToggleOffered = viewModel::toggleSkillOffered,
            onToggleSought = viewModel::toggleSkillSought,
            onAddOfferedClick = { pickerMode = PickerMode.OFFERED },
            onAddSoughtClick = { pickerMode = PickerMode.SOUGHT },
            onSaveClick = { viewModel.saveProfile(onSaved = onSaved) },
            modifier = Modifier.padding(innerPadding)
        )
    }

    // ── Bottom sheet (open when pickerMode != null) ──
    if (pickerMode != null) {
        val isOffered = pickerMode == PickerMode.OFFERED
        val title = stringResource(
            if (isOffered) R.string.skill_search_add_title
            else R.string.skill_search_add_interest_title
        )
        val available = rememberAvailableSkills() + customSkills
        val selected = if (isOffered) skillsOffered else skillsSought

        SkillPickerBottomSheet(
            title = title,
            availableSkills = available.distinct(),
            selectedSkills = selected,
            onSkillSelected = { skill ->
                if (isOffered) viewModel.toggleSkillOffered(skill)
                else viewModel.toggleSkillSought(skill)
            },
            onCustomSkillAdded = { skill ->
                viewModel.addCustomSkill(skill, isOffered = isOffered)
            },
            onDismiss = { pickerMode = null }
        )
    }
}

/**
 * Builds the catalog of predefined skills from strings.xml.
 * Only readable inside a @Composable because it needs stringResource().
 */
@Composable
private fun rememberAvailableSkills(): List<String> {
    return listOf(
        stringResource(R.string.skill_english),
        stringResource(R.string.skill_guitar),
        stringResource(R.string.skill_piano),
        stringResource(R.string.skill_programming),
        stringResource(R.string.skill_python),
        stringResource(R.string.skill_javascript),
        stringResource(R.string.skill_java),
        stringResource(R.string.skill_react),
        stringResource(R.string.skill_marketing),
        stringResource(R.string.skill_photography),
        stringResource(R.string.skill_cooking),
        stringResource(R.string.skill_graphic_design),
        stringResource(R.string.skill_french),
        stringResource(R.string.skill_german),
        stringResource(R.string.skill_singing),
        stringResource(R.string.skill_drums),
        stringResource(R.string.skill_yoga),
        stringResource(R.string.skill_running),
        stringResource(R.string.skill_painting),
        stringResource(R.string.skill_writing),
        stringResource(R.string.skill_dance),
        stringResource(R.string.skill_chess),
        stringResource(R.string.skill_video_editing),
        stringResource(R.string.skill_figma),
        stringResource(R.string.skill_photoshop)
    )
}

/**
 * Edit Profile form — Stateless composable.
 */
@Composable
private fun EditProfileForm(
    fullName: String,
    bio: String,
    country: String,
    skillsOffered: List<String>,
    skillsSought: List<String>,
    avatarUrl: String?,
    initials: String,
    isSaving: Boolean,
    errorMessage: String?,
    onFullNameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onToggleOffered: (String) -> Unit,
    onToggleSought: (String) -> Unit,
    onAddOfferedClick: () -> Unit,
    onAddSoughtClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

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
            maxLength = 60,
            contentDescriptionLabel = stringResource(R.string.edit_profile_name_label),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        DexterTextField(
            value = country,
            onValueChange = onCountryChange,
            label = stringResource(R.string.onboard_country_label),
            placeholder = stringResource(R.string.onboard_country_placeholder),
            maxLength = 60,
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
            maxLength = 300,
            contentDescriptionLabel = stringResource(R.string.edit_profile_about_label),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Skills OFFERED section ──────────────────────────
        SkillsEditableSection(
            title = stringResource(R.string.edit_profile_skills_offered_title),
            skills = skillsOffered,
            isError = false,
            onChipClick = onToggleOffered,
            onAddClick = onAddOfferedClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Skills SOUGHT section ───────────────────────────
        SkillsEditableSection(
            title = stringResource(R.string.edit_profile_skills_sought_title),
            skills = skillsSought,
            isError = true,
            onChipClick = onToggleSought,
            onAddClick = onAddSoughtClick
        )

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

/**
 * One skills group: title + chips wrapped in rows of 3 + a "+" chip at the end.
 * Tapping a chip toggles selection (removes the skill). Tapping "+" opens the picker.
 */
@Composable
private fun SkillsEditableSection(
    title: String,
    skills: List<String>,
    isError: Boolean,
    onChipClick: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // All visible items = skills + "+"
        val items: List<Any> = skills + "__ADD__"

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items.chunked(3).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { item ->
                        if (item == "__ADD__") {
                            AddSkillChip(onClick = onAddClick)
                        } else {
                            val skill = item as String
                            SkillChip(
                                label = skill,
                                selected = !isError,
                                isError = isError,
                                onClick = { onChipClick(skill) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * A "+" chip used at the end of a skills group to open the picker.
 * Dashed outline styling to differentiate from regular chips.
 */
@Composable
private fun AddSkillChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .wrapContentWidth()
            .clip(shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = shape
            )
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(R.string.cd_add_skill),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
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
            skillsOffered = listOf("Inglés", "Guitarra", "Marketing"),
            skillsSought = listOf("Fotografía", "Piano"),
            avatarUrl = null,
            initials = "AG",
            isSaving = false,
            errorMessage = null,
            onFullNameChange = {},
            onBioChange = {},
            onCountryChange = {},
            onToggleOffered = {},
            onToggleSought = {},
            onAddOfferedClick = {},
            onAddSoughtClick = {},
            onSaveClick = {}
        )
    }
}
