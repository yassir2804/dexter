package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dexter.app.R
import com.dexter.app.presentation.ui.components.AuthFooterLink
import com.dexter.app.presentation.ui.components.DexterPasswordField
import com.dexter.app.presentation.ui.components.DexterTextField
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.LoginState
import com.dexter.app.presentation.viewmodel.LoginViewModel

/**
 * LoginScreen — Stateful composable.
 *
 * Collects state from [LoginViewModel] and delegates all rendering
 * to [LoginContent]. Navigation is handled via lambdas — the
 * NavController never enters this composable, following the
 * navigation guide: "Pass navigation callbacks as parameters instead
 * of the NavController directly." The NavController only lives in
 * NavGraph.kt.
 *
 * @param loginViewModel ViewModel that manages login state and business logic.
 * @param onLoginSuccess Callback invoked when authentication succeeds.
 * @param onForgotPasswordClick Callback invoked when the user taps "Forgot password".
 * @param onRegisterClick Callback invoked when the user taps the register link.
 * @param paddingValues Scaffold padding forwarded from the parent layout.
 * @param modifier Optional [Modifier] applied to the root layout.
 */
@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    val loginState by loginViewModel.loginState.collectAsState()
    val email by loginViewModel.email.collectAsState()
    val password by loginViewModel.password.collectAsState()
    val passwordVisible by loginViewModel.passwordVisible.collectAsState()

    // Navigate to Home on successful login
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess()
        }
    }

    LoginContent(
        email = email,
        password = password,
        passwordVisible = passwordVisible,
        loginState = loginState,
        onEmailChange = loginViewModel::onEmailChange,
        onPasswordChange = loginViewModel::onPasswordChange,
        onTogglePasswordVisibility = loginViewModel::togglePasswordVisibility,
        onLoginClick = loginViewModel::login,
        onForgotPasswordClick = onForgotPasswordClick,
        onRegisterClick = onRegisterClick,
        paddingValues = paddingValues,
        modifier = modifier
    )
}

/**
 * LoginContent — Stateless composable.
 *
 * Receives plain data and lambdas; can be previewed and tested
 * without a ViewModel or NavController.
 *
 * The form is always visible regardless of [loginState] because
 * login errors and loading are communicated inline (error text
 * above the button, spinner inside the button) — the user must
 * always be able to read and correct their credentials without
 * the screen replacing itself.
 *
 * @param email Current value of the email input.
 * @param password Current value of the password input.
 * @param passwordVisible Whether the password is shown in plain text.
 * @param loginState Current [LoginState] emitted by the ViewModel.
 * @param onEmailChange Invoked on every email keystroke.
 * @param onPasswordChange Invoked on every password keystroke.
 * @param onTogglePasswordVisibility Invoked when the visibility icon is tapped.
 * @param onLoginClick Invoked when the login button is tapped.
 * @param onForgotPasswordClick Invoked when "Forgot password?" is tapped.
 * @param onRegisterClick Invoked when the register link is tapped.
 * @param paddingValues Scaffold padding forwarded from the parent layout.
 * @param modifier Optional [Modifier] applied to the root column.
 */
@Composable
private fun LoginContent(
    email: String,
    password: String,
    passwordVisible: Boolean,
    loginState: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // ── Brand Section ───────────────────────────────────────────
        BrandLogo()

        Spacer(modifier = Modifier.height(40.dp))

        // ── Email Field ─────────────────────────────────────────────
        DexterTextField(
            value = email,
            onValueChange = onEmailChange,
            label = stringResource(R.string.login_email_label),
            placeholder = stringResource(R.string.login_email_placeholder),
            leadingIcon = Icons.Default.Email,
            keyboardType = KeyboardType.Email,
            contentDescriptionLabel = stringResource(R.string.cd_email_input),
            modifier = Modifier
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Password Field ──────────────────────────────────────────
        DexterPasswordField(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(R.string.login_password_label),
            passwordVisible = passwordVisible,
            onToggleVisibility = onTogglePasswordVisibility,
            placeholder = stringResource(R.string.login_password_placeholder),
            modifier = Modifier
        )

        // ── Forgot Password ─────────────────────────────────────────
        TextButton(
            onClick = onForgotPasswordClick,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = stringResource(R.string.login_forgot_password),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Error Message ───────────────────────────────────────────
        // Shown inline so the user can read and fix credentials
        // without the form disappearing from the screen.
        if (loginState is LoginState.Error) {
            val errorDescription = stringResource(R.string.login_error_content_description, loginState.message)
            Text(
                text = loginState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .semantics {
                        contentDescription = errorDescription
                    }
            )
        }

        // ── Login Button ────────────────────────────────────────────
        // Loading is communicated via a spinner inside the button,
        // not by replacing the screen, so the form stays visible.
        val loginButtonCD = stringResource(R.string.cd_login_button)
        Button(
            onClick = onLoginClick,
            enabled = loginState !is LoginState.Loading,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .semantics { contentDescription = loginButtonCD }
        ) {
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = stringResource(R.string.login_button),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Register Link ───────────────────────────────────────────
        AuthFooterLink(
            prefixText = stringResource(R.string.login_no_account),
            linkText = stringResource(R.string.login_register_link),
            onClick = onRegisterClick,
            contentDescriptionLabel = stringResource(R.string.cd_register_link),
            modifier = Modifier
        )
    }
}

// ── Brand Logo Component ────────────────────────────────────────────

/**
 * BrandLogo — Displays the app icon, name, and tagline.
 *
 * Composed of two nested [Surface] composables (outer 72×72 dp with
 * primaryContainer color, inner 52×52 dp with primary color) centered
 * around a [Icons.Default.Layers] icon, followed by the app name and
 * tagline as [Text] elements.
 *
 * @param modifier Optional [Modifier] applied to the root column.
 */
@Composable
private fun BrandLogo(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        // Outer container: 72x72, radius 24, primaryContainer
        Surface(
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Inner box: 52x52, radius 16, primary
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = stringResource(R.string.app_name),
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // App name
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 30.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Subtitle
        Text(
            text = stringResource(R.string.app_tagline),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ── Previews ────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    AppTheme(dynamicColor = false) {
        com.dexter.app.presentation.ui.layout.MainLayout(paddingValues = PaddingValues(16.dp)) {
            LoginContent(
                email = "",
                password = "",
                passwordVisible = false,
                loginState = LoginState.Empty,
                onEmailChange = {},
                onPasswordChange = {},
                onTogglePasswordVisibility = {},
                onLoginClick = {},
                onForgotPasswordClick = {},
                onRegisterClick = {},

                paddingValues = PaddingValues(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenErrorPreview() {
    AppTheme(dynamicColor = false) {
        com.dexter.app.presentation.ui.layout.MainLayout(paddingValues = PaddingValues(16.dp)) {
            LoginContent(
            email = "test@test.com",
            password = "wrong",
            passwordVisible = false,
            loginState = LoginState.Error(stringResource(R.string.login_error_invalid_credentials)),
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLoginClick = {},
            onForgotPasswordClick = {},
            onRegisterClick = {},
            paddingValues = PaddingValues(16.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenLoadingPreview() {
    AppTheme(dynamicColor = false) {
        com.dexter.app.presentation.ui.layout.MainLayout(paddingValues = PaddingValues(16.dp)) {
            LoginContent(
            email = "test@test.com",
            password = "password123",
            passwordVisible = false,
            loginState = LoginState.Loading,
            onEmailChange = {},
            onPasswordChange = {},
            onTogglePasswordVisibility = {},
            onLoginClick = {},
            onForgotPasswordClick = {},
            onRegisterClick = {},
            paddingValues = PaddingValues(16.dp)
        )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BrandLogoPreview() {
    AppTheme(dynamicColor = false) {
        BrandLogo()
    }
}
