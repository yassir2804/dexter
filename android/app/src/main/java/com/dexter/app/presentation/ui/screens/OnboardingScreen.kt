package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.presentation.ui.components.DexterTextField
import com.dexter.app.presentation.ui.components.SkillChip
import com.dexter.app.presentation.ui.components.SkillPickerBottomSheet
import com.dexter.app.presentation.ui.components.StepDots
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.RegisterState
import com.dexter.app.presentation.viewmodel.RegisterViewModel

/**
 * Onboarding screen (step 2/3) — Stateful composable.
 *
 * Shares RegisterViewModel with WelcomeScreen via auth_graph scope.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    registerViewModel: RegisterViewModel,
    email: String,
    password: String,
    onFinishClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by registerViewModel.state.collectAsState()
    val name by registerViewModel.name.collectAsState()
    val country by registerViewModel.country.collectAsState()
    val phone by registerViewModel.phone.collectAsState()
    val skillsToTeach by registerViewModel.skillsToTeach.collectAsState()
    val skillsToLearn by registerViewModel.skillsToLearn.collectAsState()
    val availableSkillsToTeach by registerViewModel.availableSkillsToTeach.collectAsState()
    val availableSkillsToLearn by registerViewModel.availableSkillsToLearn.collectAsState()
    val customSkills by registerViewModel.customSkills.collectAsState()
    val availabilityDays by registerViewModel.availabilityDays.collectAsState()

    // Load available skills on first composition
    LaunchedEffect(Unit) {
        registerViewModel.loadAvailableSkills()
    }

    OnboardingContent(
        state = state,
        name = name,
        country = country,
        phone = phone,
        skillsToTeach = skillsToTeach,
        skillsToLearn = skillsToLearn,
        availableSkillsToTeach = availableSkillsToTeach,
        availableSkillsToLearn = availableSkillsToLearn,
        customSkills = customSkills,
        availabilityDays = availabilityDays,
        onNameChange = registerViewModel::onNameChange,
        onCountryChange = registerViewModel::onCountryChange,
        onPhoneChange = registerViewModel::onPhoneChange,
        onToggleTeach = registerViewModel::toggleSkillToTeach,
        onToggleLearn = registerViewModel::toggleSkillToLearn,
        onAddCustomTeach = { registerViewModel.addCustomSkill(it, isTeach = true) },
        onAddCustomLearn = { registerViewModel.addCustomSkill(it, isTeach = false) },
        onToggleAvailability = registerViewModel::toggleAvailabilityDay,
        onFinishClick = {
            registerViewModel.register(
                email = email,
                password = password,
                onRegisterSuccess = onFinishClick
            )
        },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

/**
 * Onboarding content — Stateless composable.
 */
@Composable
private fun OnboardingContent(
    state: RegisterState,
    name: String,
    country: String,
    phone: String,
    skillsToTeach: List<String>,
    skillsToLearn: List<String>,
    availableSkillsToTeach: List<String>,
    availableSkillsToLearn: List<String>,
    customSkills: List<String>,
    availabilityDays: Set<Int>,
    onNameChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onToggleTeach: (String) -> Unit,
    onToggleLearn: (String) -> Unit,
    onAddCustomTeach: (String) -> Unit,
    onAddCustomLearn: (String) -> Unit,
    onToggleAvailability: (Int) -> Unit,
    onFinishClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = state is RegisterState.Loading
    val errorMessage = when (state) {
        is RegisterState.Error -> stringResource(state.messageRes)
        is RegisterState.ApiError -> state.message
        else -> null
    }

    // Picker state
    var pickerMode by remember { mutableStateOf<OnboardPickerMode?>(null) }

    // Build available skills list (backend + custom)
    val availableTeachForPicker = (availableSkillsToTeach + customSkills).distinct()
    val availableLearnForPicker = (availableSkillsToLearn + customSkills).distinct()

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top App Bar ─────────────────────────────────────
        val backDescription = stringResource(R.string.cd_back_to_login)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.semantics { contentDescription = backDescription }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = stringResource(R.string.onboard_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // ── Step dots ───────────────────────────────────
            StepDots(totalSteps = 3, currentStep = 1)

            Spacer(modifier = Modifier.height(24.dp))

            // ── Avatar placeholder ──────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { /* TODO: photo picker */ }) {
                Text(
                    text = stringResource(R.string.onboard_add_photo),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Name field ──────────────────────────────────
            DexterTextField(
                value = name,
                onValueChange = onNameChange,
                label = stringResource(R.string.onboard_name_label),
                placeholder = stringResource(R.string.onboard_name_placeholder),
                maxLength = 60,
                contentDescriptionLabel = stringResource(R.string.onboard_name_label),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Phone field ──────────────────────────────────
            DexterTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = stringResource(R.string.onboard_phone_label),
                placeholder = stringResource(R.string.onboard_phone_placeholder),
                maxLength = 20,
                contentDescriptionLabel = stringResource(R.string.onboard_phone_label),
                keyboardType = KeyboardType.Phone,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Country field ───────────────────────────────
            DexterTextField(
                value = country,
                onValueChange = onCountryChange,
                label = stringResource(R.string.onboard_country_label),
                placeholder = stringResource(R.string.onboard_country_placeholder),
                maxLength = 60,
                contentDescriptionLabel = stringResource(R.string.onboard_country_label),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Skills to teach ─────────────────────────────
            Text(
                text = stringResource(R.string.onboard_skills_teach),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Show selected skills + "+" button
            if (skillsToTeach.isEmpty()) {
                TextButton(
                    onClick = { pickerMode = OnboardPickerMode.TEACH },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.onboard_add_skill),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    skillsToTeach.forEach { skill ->
                        SkillChip(
                            label = skill,
                            selected = true,
                            onClick = { onToggleTeach(skill) }
                        )
                    }
                    IconButton(
                        onClick = { pickerMode = OnboardPickerMode.TEACH },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.cd_add_skill),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Skills to learn ─────────────────────────────
            Text(
                text = stringResource(R.string.onboard_skills_learn),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Show selected skills + "+" button
            if (skillsToLearn.isEmpty()) {
                TextButton(
                    onClick = { pickerMode = OnboardPickerMode.LEARN },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = stringResource(R.string.onboard_add_skill),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    skillsToLearn.forEach { skill ->
                        SkillChip(
                            label = skill,
                            selected = true,
                            onClick = { onToggleLearn(skill) }
                        )
                    }
                    IconButton(
                        onClick = { pickerMode = OnboardPickerMode.LEARN },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.cd_add_skill),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Availability (weekdays) ─────────────────────
            Text(
                text = stringResource(R.string.onboard_availability),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            val dayLabels = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sab", "Dom")
            AvailabilityChipWrap(
                dayLabels = dayLabels,
                selectedDays = availabilityDays,
                onToggleDay = { label ->
                    val dayOfWeek = dayLabels.indexOf(label) + 1
                    onToggleAvailability(dayOfWeek)
                }
            )

            // ── Error message ───────────────────────────────
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Finish button ───────────────────────────────
            Button(
                onClick = onFinishClick,
                enabled = !isLoading,
                shape = RoundedCornerShape(50),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text(
                        text = stringResource(R.string.onboard_finish),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ── Bottom sheet (open when pickerMode != null) ──
    if (pickerMode != null) {
        val isTeach = pickerMode == OnboardPickerMode.TEACH
        val title = stringResource(
            if (isTeach) R.string.skill_search_add_title
            else R.string.skill_search_add_interest_title
        )
        val available = if (isTeach) availableTeachForPicker else availableLearnForPicker
        val selected = if (isTeach) skillsToTeach else skillsToLearn

        SkillPickerBottomSheet(
            title = title,
            availableSkills = available,
            selectedSkills = selected,
            onSkillSelected = { skill ->
                if (isTeach) onToggleTeach(skill) else onToggleLearn(skill)
            },
            onCustomSkillAdded = { skill ->
                if (isTeach) onAddCustomTeach(skill) else onAddCustomLearn(skill)
            },
            onDismiss = { pickerMode = null }
        )
    }
}

// Which picker is currently open
private enum class OnboardPickerMode { TEACH, LEARN }

/**
 * Simple chip wrap layout that avoids FlowRow (crashes with Compose BOM 2024.09.00).
 * Groups chips into rows of ~3 items.
 */
@Composable
private fun ChipWrap(
    options: List<String>,
    selectedOptions: List<String>,
    onToggle: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        options.chunked(3).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { option ->
                    val isSelected = option in selectedOptions
                    SkillChip(
                        label = option,
                        selected = isSelected && !isError,
                        isError = isSelected && isError,
                        onClick = { onToggle(option) }
                    )
                }
            }
        }
    }
}

/**
 * Availability chip wrap for weekday selection (Mon–Sun).
 * Maps day labels to 1–7 (ISO-8601) internally.
 */
@Composable
private fun AvailabilityChipWrap(
    dayLabels: List<String>,
    selectedDays: Set<Int>,
    onToggleDay: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        dayLabels.chunked(4).forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEachIndexed { index, label ->
                    val dayOfWeek = dayLabels.indexOf(label) + 1
                    val isSelected = dayOfWeek in selectedDays
                    SkillChip(
                        label = label,
                        selected = isSelected,
                        isError = false,
                        onClick = { onToggleDay(label) }
                    )
                }
            }
        }
    }
}

// ── Previews ────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingPreview() {
    AppTheme(dynamicColor = false) {
        OnboardingContent(
            state = RegisterState.Idle,
            name = "",
            country = "",
            phone = "",
            skillsToTeach = emptyList(),
            skillsToLearn = emptyList(),
            availableSkillsToTeach = listOf("Guitarra", "Piano", "Inglés"),
            availableSkillsToLearn = listOf("Yoga", "Pintura", "Programación"),
            customSkills = emptyList(),
            availabilityDays = emptySet(),
            onNameChange = {},
            onCountryChange = {},
            onPhoneChange = {},
            onToggleTeach = {},
            onToggleLearn = {},
            onAddCustomTeach = {},
            onAddCustomLearn = {},
            onToggleAvailability = {},
            onFinishClick = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingWithSelectionsPreview() {
    AppTheme(dynamicColor = false) {
        OnboardingContent(
            state = RegisterState.Idle,
            name = "Carlos Méndez",
            country = "Costa Rica",
            phone = "+506 8765 4321",
            skillsToTeach = listOf("Programación", "Música"),
            skillsToLearn = listOf("Yoga", "Pintura"),
            availableSkillsToTeach = listOf("Guitarra", "Piano", "Inglés", "Programación", "Música"),
            availableSkillsToLearn = listOf("Yoga", "Pintura", "Programación", "Fotografía", "Cocina"),
            customSkills = emptyList(),
            availabilityDays = setOf(1, 2, 3, 4, 5),
            onNameChange = {},
            onCountryChange = {},
            onPhoneChange = {},
            onToggleTeach = {},
            onToggleLearn = {},
            onAddCustomTeach = {},
            onAddCustomLearn = {},
            onToggleAvailability = {},
            onFinishClick = {},
            onBackClick = {}
        )
    }
}
