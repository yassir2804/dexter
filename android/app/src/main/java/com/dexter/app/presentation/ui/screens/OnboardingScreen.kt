package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.dexter.app.presentation.ui.components.StepDots
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.RegisterState
import com.dexter.app.presentation.viewmodel.RegisterViewModel

/**
 * Onboarding screen (step 2/3) — Stateful composable.
 *
 * Shares RegisterViewModel with WelcomeScreen via auth_graph scope.
 */
@Composable
fun OnboardingScreen(
    registerViewModel: RegisterViewModel,
    onFinishClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by registerViewModel.state.collectAsState()
    val name by registerViewModel.name.collectAsState()
    val country by registerViewModel.country.collectAsState()
    val skillsToTeach by registerViewModel.skillsToTeach.collectAsState()
    val skillsToLearn by registerViewModel.skillsToLearn.collectAsState()

    LaunchedEffect(state) {
        if (state is RegisterState.Success) {
            onFinishClick()
            registerViewModel.resetState()
        }
    }

    OnboardingContent(
        state = state,
        name = name,
        country = country,
        skillsToTeach = skillsToTeach,
        skillsToLearn = skillsToLearn,
        onNameChange = registerViewModel::onNameChange,
        onCountryChange = registerViewModel::onCountryChange,
        onToggleTeach = registerViewModel::toggleSkillToTeach,
        onToggleLearn = registerViewModel::toggleSkillToLearn,
        onFinishClick = registerViewModel::register,
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
    skillsToTeach: List<String>,
    skillsToLearn: List<String>,
    onNameChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onToggleTeach: (String) -> Unit,
    onToggleLearn: (String) -> Unit,
    onFinishClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = state is RegisterState.Loading
    val errorMessage = (state as? RegisterState.Error)?.messageRes?.let { stringResource(it) }

    val teachOptions = listOf(
        stringResource(R.string.skill_programming),
        stringResource(R.string.skill_graphic_design),
        stringResource(R.string.skill_marketing),
        stringResource(R.string.skill_photography),
        stringResource(R.string.skill_music),
        stringResource(R.string.skill_languages),
        stringResource(R.string.skill_cooking),
        stringResource(R.string.skill_fitness)
    )
    val learnOptions = listOf(
        stringResource(R.string.skill_yoga),
        stringResource(R.string.skill_painting),
        stringResource(R.string.skill_writing),
        stringResource(R.string.skill_videogames),
        stringResource(R.string.skill_gardening),
        stringResource(R.string.skill_dance),
        stringResource(R.string.skill_cinema),
        stringResource(R.string.skill_meditation)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
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
                contentDescriptionLabel = stringResource(R.string.onboard_name_label),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Country field ───────────────────────────────
            DexterTextField(
                value = country,
                onValueChange = onCountryChange,
                label = stringResource(R.string.onboard_country_label),
                placeholder = stringResource(R.string.onboard_country_placeholder),
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

            ChipWrap(
                options = teachOptions,
                selectedOptions = skillsToTeach,
                onToggle = onToggleTeach,
                isError = false
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Skills to learn ─────────────────────────────
            Text(
                text = stringResource(R.string.onboard_skills_learn),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            ChipWrap(
                options = learnOptions,
                selectedOptions = skillsToLearn,
                onToggle = onToggleLearn,
                isError = true
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
}

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

// ── Previews ────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingPreview() {
    AppTheme(dynamicColor = false) {
        OnboardingContent(
            state = RegisterState.Idle,
            name = "",
            country = "",
            skillsToTeach = emptyList(),
            skillsToLearn = emptyList(),
            onNameChange = {},
            onCountryChange = {},
            onToggleTeach = {},
            onToggleLearn = {},
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
            skillsToTeach = listOf("Programación", "Música"),
            skillsToLearn = listOf("Yoga", "Pintura"),
            onNameChange = {},
            onCountryChange = {},
            onToggleTeach = {},
            onToggleLearn = {},
            onFinishClick = {},
            onBackClick = {}
        )
    }
}
