package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterTiltShift
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.presentation.ui.components.AuthFooterLink
import com.dexter.app.presentation.ui.components.DexterPasswordField
import com.dexter.app.presentation.ui.components.DexterTextField
import com.dexter.app.presentation.ui.components.StepDots
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.RegisterState
import com.dexter.app.presentation.viewmodel.RegisterViewModel

/**
 * Welcome screen (step 1/3) — Stateful composable.
 *
 * Receives navigation callbacks as lambdas (NOT the NavController).
 */
@Composable
fun WelcomeScreen(
    registerViewModel: RegisterViewModel,
    onContinueClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by registerViewModel.state.collectAsState()
    val email by registerViewModel.email.collectAsState()
    val password by registerViewModel.password.collectAsState()
    val confirmPassword by registerViewModel.confirmPassword.collectAsState()
    val passwordVisible by registerViewModel.passwordVisible.collectAsState()
    val confirmPasswordVisible by registerViewModel.confirmPasswordVisible.collectAsState()

    WelcomeContent(
        state = state,
        email = email,
        password = password,
        confirmPassword = confirmPassword,
        passwordVisible = passwordVisible,
        confirmPasswordVisible = confirmPasswordVisible,
        onEmailChange = registerViewModel::onEmailChange,
        onPasswordChange = registerViewModel::onPasswordChange,
        onConfirmPasswordChange = registerViewModel::onConfirmPasswordChange,
        onTogglePassword = registerViewModel::togglePasswordVisibility,
        onToggleConfirmPassword = registerViewModel::toggleConfirmPasswordVisibility,
        onContinueClick = {
            if (registerViewModel.validateWelcomeFields()) {
                onContinueClick()
            }
        },
        onLoginClick = onLoginClick,
        modifier = modifier
    )
}

/**
 * Welcome content — Stateless composable.
 */
@Composable
private fun WelcomeContent(
    state: RegisterState,
    email: String,
    password: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePassword: () -> Unit,
    onToggleConfirmPassword: () -> Unit,
    onContinueClick: () -> Unit,
    onLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLoading = state is RegisterState.Loading
    val errorMessage = (state as? RegisterState.Error)?.messageRes?.let { stringResource(it) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp, bottom = 16.dp)
    ) {
        // ── Step dots ───────────────────────────────────────
        StepDots(totalSteps = 3, currentStep = 0)

        Spacer(modifier = Modifier.height(24.dp))

        // ── Title ───────────────────────────────────────────
        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Subtitle ────────────────────────────────────────
        Text(
            text = stringResource(R.string.app_welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Email field ─────────────────────────────────────
        DexterTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.login_email_label),
            placeholder = stringResource(R.string.login_email_placeholder),
            leadingIcon = Icons.Default.FilterTiltShift,
            contentDescriptionLabel = stringResource(R.string.cd_email_input),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Password field ──────────────────────────────────
        DexterPasswordField(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.login_password_label),
            passwordVisible = passwordVisible,
            onToggleVisibility = onTogglePassword,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ── Confirm password field ──────────────────────────
        DexterPasswordField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = stringResource(R.string.welcome_confirm_password_label),
            passwordVisible = confirmPasswordVisible,
            onToggleVisibility = onToggleConfirmPassword,
            modifier = Modifier.fillMaxWidth()
        )

        // ── Error message ───────────────────────────────────
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Benefits ────────────────────────────────────────
        val benefits = listOf(
            Icons.Default.FilterTiltShift to R.string.welcome_benefit_1,
            Icons.Default.People to R.string.welcome_benefit_2,
            Icons.Default.CalendarMonth to R.string.welcome_benefit_3,
            Icons.Default.Star to R.string.welcome_benefit_4
        )

        benefits.forEach { (icon, textRes) ->
            BenefitCard(icon = icon, text = stringResource(textRes))
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // ── Continue button ─────────────────────────────────
        Button(
            onClick = onContinueClick,
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
                    text = stringResource(R.string.welcome_continue),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Login link ──────────────────────────────────────
        AuthFooterLink(
            prefixText = stringResource(R.string.welcome_has_account),
            linkText = stringResource(R.string.welcome_login_link),
            onClick = onLoginClick,
            contentDescriptionLabel = stringResource(R.string.welcome_login_link),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ── Benefit Card ────────────────────────────────────────────────

@Composable
private fun BenefitCard(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(20.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// ── Previews ────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomePreview() {
    AppTheme(dynamicColor = false) {
        WelcomeContent(
            state = RegisterState.Idle,
            email = "",
            password = "",
            confirmPassword = "",
            passwordVisible = false,
            confirmPasswordVisible = false,
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePassword = {},
            onToggleConfirmPassword = {},
            onContinueClick = {},
            onLoginClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun WelcomeErrorPreview() {
    AppTheme(dynamicColor = false) {
        WelcomeContent(
            state = RegisterState.Error(R.string.error_password_mismatch),
            email = "test@example.com",
            password = "123456",
            confirmPassword = "123457",
            passwordVisible = false,
            confirmPasswordVisible = false,
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePassword = {},
            onToggleConfirmPassword = {},
            onContinueClick = {},
            onLoginClick = {}
        )
    }
}
