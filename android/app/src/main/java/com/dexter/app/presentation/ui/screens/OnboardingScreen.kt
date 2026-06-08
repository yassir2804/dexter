package com.dexter.app.presentation.ui.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import com.dexter.app.presentation.util.ImageUtils
import com.dexter.app.presentation.viewmodel.RegisterState
import com.dexter.app.presentation.viewmodel.RegisterViewModel

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
    val avatarBase64 by registerViewModel.avatarBase64.collectAsState()
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Log.d("OnboardingScreen", "Photo picker returned uri=$uri")
        if (uri != null) {
            val base64 = ImageUtils.uriToBase64(context, uri)
            Log.d("OnboardingScreen", "Base64 result: ${if (base64 != null) "${base64.length} chars" else "null"}")
            registerViewModel.onAvatarBase64Change(base64)
        }
    }

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
        avatarBase64 = avatarBase64,
        onNameChange = registerViewModel::onNameChange,
        onCountryChange = registerViewModel::onCountryChange,
        onPhoneChange = registerViewModel::onPhoneChange,
        onToggleTeach = registerViewModel::toggleSkillToTeach,
        onToggleLearn = registerViewModel::toggleSkillToLearn,
        onAddCustomTeach = { registerViewModel.addCustomSkill(it, isTeach = true) },
        onAddCustomLearn = { registerViewModel.addCustomSkill(it, isTeach = false) },
        onToggleAvailability = registerViewModel::toggleAvailabilityDay,
        onSearchSkills = { query ->
            if (query.length >= 2) registerViewModel.searchSkills(query)
        },
        onPickPhoto = {
            Log.d("OnboardingScreen", "Launching photo picker")
            photoPickerLauncher.launch("image/*")
        },
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
    avatarBase64: String?,
    onNameChange: (String) -> Unit,
    onCountryChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onToggleTeach: (String) -> Unit,
    onToggleLearn: (String) -> Unit,
    onAddCustomTeach: (String) -> Unit,
    onAddCustomLearn: (String) -> Unit,
    onToggleAvailability: (Int) -> Unit,
    onSearchSkills: (String) -> Unit,
    onPickPhoto: () -> Unit,
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

    // Internal page: 0 = info+skills, 1 = availability
    var page by remember { mutableIntStateOf(0) }
    var pickerMode by remember { mutableStateOf<OnboardPickerMode?>(null) }

    val availableTeachForPicker = (availableSkillsToTeach + customSkills).distinct()
    val availableLearnForPicker = (availableSkillsToLearn + customSkills).distinct()

    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
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
                onClick = { if (page > 0) page-- else onBackClick() },
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

        // ── Scrollable content ──────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            // Step dots: step 0=Welcome, step 1=this page 0, step 2=this page 1
            StepDots(totalSteps = 3, currentStep = page + 1)

            Spacer(modifier = Modifier.height(24.dp))

            if (page == 0) {
                // ═══ PAGE 0: Personal info + Skills ═══

                val avatarBitmap = remember(avatarBase64) {
                    avatarBase64?.let { ImageUtils.base64ToBitmap(it) }
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        .clickable(onClick = onPickPhoto)
                ) {
                    if (avatarBitmap != null) {
                        Image(
                            bitmap = avatarBitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = onPickPhoto) {
                    Text(
                        text = stringResource(R.string.onboard_add_photo),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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

                // Skills to teach — show top 3 popular as suggestions
                OnboardSkillsSection(
                    title = stringResource(R.string.onboard_skills_teach),
                    skills = skillsToTeach,
                    suggestions = availableSkillsToTeach.take(3),
                    isError = false,
                    onChipClick = onToggleTeach,
                    onAddClick = { pickerMode = OnboardPickerMode.TEACH }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Skills to learn — show top 3 popular as suggestions
                OnboardSkillsSection(
                    title = stringResource(R.string.onboard_skills_learn),
                    skills = skillsToLearn,
                    suggestions = availableSkillsToLearn.take(3),
                    isError = true,
                    onChipClick = onToggleLearn,
                    onAddClick = { pickerMode = OnboardPickerMode.LEARN }
                )

                // Error on page 0
                if (errorMessage != null && !isLoading) {
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

                Button(
                    onClick = { page = 1 },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = stringResource(R.string.onboard_next),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

            } else {
                // ═══ PAGE 1: Availability ═══

                Text(
                    text = stringResource(R.string.onboard_availability_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.onboard_availability_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                val dayLabels = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sab", "Dom")
                val dayFullLabels = listOf(
                    stringResource(R.string.day_monday),
                    stringResource(R.string.day_tuesday),
                    stringResource(R.string.day_wednesday),
                    stringResource(R.string.day_thursday),
                    stringResource(R.string.day_friday),
                    stringResource(R.string.day_saturday),
                    stringResource(R.string.day_sunday)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    dayFullLabels.forEachIndexed { index, label ->
                        val dayOfWeek = index + 1
                        val isSelected = dayOfWeek in availabilityDays
                        DaySelectionRow(
                            label = label,
                            shortLabel = dayLabels[index],
                            isSelected = isSelected,
                            onClick = { onToggleAvailability(dayOfWeek) }
                        )
                    }
                }

                // Error on page 1
                if (errorMessage != null && !isLoading) {
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

    // ── Skill picker bottom sheet ──
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
            onSearchQueryChanged = { query -> onSearchSkills(query) },
            onDismiss = { pickerMode = null }
        )
    }
}

private enum class OnboardPickerMode { TEACH, LEARN }

@Composable
private fun OnboardSkillsSection(
    title: String,
    skills: List<String>,
    suggestions: List<String> = emptyList(),
    isError: Boolean,
    onChipClick: (String) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Show selected skills + unselected suggestions + "+" button
        val unselectedSuggestions = suggestions.filter { it !in skills }
        val allItems: List<Any> = skills + unselectedSuggestions + "__ADD__"

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            allItems.chunked(3).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { item ->
                        if (item == "__ADD__") {
                            OnboardAddChip(onClick = onAddClick)
                        } else {
                            val skill = item as String
                            val isSelected = skill in skills
                            SkillChip(
                                label = skill,
                                selected = isSelected && !isError,
                                isError = isSelected && isError,
                                onClick = { onChipClick(skill) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardAddChip(
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
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
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

@Composable
private fun DaySelectionRow(
    label: String,
    shortLabel: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    val bgColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
    else MaterialTheme.colorScheme.surface
    val borderColor = if (isSelected) MaterialTheme.colorScheme.secondary
    else MaterialTheme.colorScheme.outlineVariant
    val textColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
    else MaterialTheme.colorScheme.onSurface

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(1.dp, borderColor, shape)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                )
        ) {
            Text(
                text = shortLabel.take(2),
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor
        )

        Spacer(modifier = Modifier.weight(1f))

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

// ── Previews ────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun OnboardingPage0Preview() {
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
            avatarBase64 = null,
            onNameChange = {},
            onCountryChange = {},
            onPhoneChange = {},
            onToggleTeach = {},
            onToggleLearn = {},
            onAddCustomTeach = {},
            onAddCustomLearn = {},
            onToggleAvailability = {},
            onSearchSkills = {},
            onPickPhoto = {},
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
            avatarBase64 = null,
            onNameChange = {},
            onCountryChange = {},
            onPhoneChange = {},
            onToggleTeach = {},
            onToggleLearn = {},
            onAddCustomTeach = {},
            onAddCustomLearn = {},
            onToggleAvailability = {},
            onSearchSkills = {},
            onPickPhoto = {},
            onFinishClick = {},
            onBackClick = {}
        )
    }
}
