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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.presentation.ui.components.DexterTextField
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.ForgotPasswordState
import com.dexter.app.presentation.viewmodel.ForgotPasswordViewModel

/**
 * Forgot Password screen — Stateful composable.
 *
 * Receives navigation callbacks as lambdas (NOT the NavController).
 */
@Composable
fun ForgotPasswordScreen(
    forgotPasswordViewModel: ForgotPasswordViewModel,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by forgotPasswordViewModel.state.collectAsState()
    val email by forgotPasswordViewModel.email.collectAsState()

    ForgotPasswordContent(
        state = state,
        email = email,
        onEmailChange = forgotPasswordViewModel::onEmailChange,
        onSendClick = forgotPasswordViewModel::sendResetEmail,
        onResendClick = forgotPasswordViewModel::resendEmail,
        onBackToLoginClick = onBackToLoginClick,
        modifier = modifier
    )
}

/**
 * Forgot Password content — Stateless composable.
 *
 * Two views based on state:
 * - Idle/Loading/Error → form to enter email
 * - Sent → confirmation with email shown
 */
@Composable
private fun ForgotPasswordContent(
    state: ForgotPasswordState,
    email: String,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onResendClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top App Bar ─────────────────────────────────────────
        ForgotPasswordTopBar(onBackToLoginClick = onBackToLoginClick)

        when (state) {
            is ForgotPasswordState.Sent -> {
                ForgotPasswordConfirmation(
                    email = state.email,
                    onBackToLoginClick = onBackToLoginClick,
                    onResendClick = onResendClick
                )
            }
            else -> {
                ForgotPasswordForm(
                    email = email,
                    onEmailChange = onEmailChange,
                    onSendClick = onSendClick,
                    isLoading = state is ForgotPasswordState.Loading,
                    errorMessage = (state as? ForgotPasswordState.Error)?.message,
                    onBackToLoginClick = onBackToLoginClick
                )
            }
        }
    }
}

// ── Top App Bar ─────────────────────────────────────────────────

@Composable
private fun ForgotPasswordTopBar(
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backDescription = stringResource(R.string.cd_back_to_login)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
        IconButton(
            onClick = onBackToLoginClick,
            modifier = Modifier.semantics { contentDescription = backDescription }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Text(
            text = stringResource(R.string.forgot_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ── Form View (Idle / Loading / Error) ──────────────────────────

@Composable
private fun ForgotPasswordForm(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 40.dp)
    ) {
        // ── Email icon circle ───────────────────────────────
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .padding(22.dp)
                    .size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Heading ─────────────────────────────────────────
        Text(
            text = stringResource(R.string.forgot_heading),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Description ─────────────────────────────────────
        Text(
            text = stringResource(R.string.forgot_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Email field ─────────────────────────────────────
        DexterTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.login_email_label),
            placeholder = stringResource(R.string.login_email_placeholder),
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            contentDescriptionLabel = stringResource(R.string.cd_email_input),
            isError = errorMessage != null,
            supportingText = errorMessage,
            modifier = Modifier.fillMaxWidth()
        )

        // ── Send button ─────────────────────────────────────
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onSendClick,
            enabled = !isLoading,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(
                    text = stringResource(R.string.forgot_send_button),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        // ── Back to login ───────────────────────────────────
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onBackToLoginClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.forgot_back_login),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ── Confirmation View (Sent) ────────────────────────────────────

@Composable
private fun ForgotPasswordConfirmation(
    email: String,
    onBackToLoginClick: () -> Unit,
    onResendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 40.dp)
    ) {
        // ── Email icon circle (secondary) ───────────────────
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(22.dp)
                    .size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Heading ─────────────────────────────────────────
        Text(
            text = stringResource(R.string.forgot_check_email),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // ── Description ─────────────────────────────────────
        Text(
            text = stringResource(R.string.forgot_sent_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Email card ──────────────────────────────────────
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Spam hint ───────────────────────────────────────
        Text(
            text = stringResource(R.string.forgot_spam_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Back to login button ────────────────────────────
        Button(
            onClick = onBackToLoginClick,
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = stringResource(R.string.forgot_back_login),
                style = MaterialTheme.typography.labelLarge
            )
        }

        // ── Resend button ───────────────────────────────────
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onResendClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.forgot_resend),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ── Previews ────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ForgotPasswordFormPreview() {
    AppTheme(dynamicColor = false) {
        ForgotPasswordContent(
            state = ForgotPasswordState.Idle,
            email = "",
            onEmailChange = {},
            onSendClick = {},
            onResendClick = {},
            onBackToLoginClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ForgotPasswordFormErrorPreview() {
    AppTheme(dynamicColor = false) {
        ForgotPasswordContent(
            state = ForgotPasswordState.Error("Ingresa un correo válido"),
            email = "correo-malo",
            onEmailChange = {},
            onSendClick = {},
            onResendClick = {},
            onBackToLoginClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ForgotPasswordConfirmationPreview() {
    AppTheme(dynamicColor = false) {
        ForgotPasswordContent(
            state = ForgotPasswordState.Sent(email = "alice@example.com"),
            email = "alice@example.com",
            onEmailChange = {},
            onSendClick = {},
            onResendClick = {},
            onBackToLoginClick = {}
        )
    }
}
