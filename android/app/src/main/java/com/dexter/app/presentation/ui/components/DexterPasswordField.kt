package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.presentation.ui.theme.AppTheme

/**
 * Password field that wraps [DexterTextField] and provides a visibility toggle.
 * @param value current password value
 * @param onValueChange called on change
 * @param label label text
 */
@Composable
fun DexterPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    passwordVisible: Boolean,
    onToggleVisibility: () -> Unit,
    placeholder: String? = null,
    modifier: Modifier = Modifier
) {
    DexterTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = placeholder,
        leadingIcon = Icons.Default.Lock,
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (passwordVisible) stringResource(R.string.toggle_hide_password) else stringResource(com.dexter.app.R.string.toggle_show_password),
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        contentDescriptionLabel = stringResource(com.dexter.app.R.string.cd_password_input),
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun DexterPasswordFieldPreviewHidden() {
    AppTheme(dynamicColor = false) {
        DexterPasswordField(
            value = "",
            onValueChange = {},
            label = stringResource(com.dexter.app.R.string.login_password_label),
            passwordVisible = false,
            onToggleVisibility = {},
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DexterPasswordFieldPreviewVisible() {
    AppTheme(dynamicColor = false) {
        DexterPasswordField(
            value = "password123",
            onValueChange = {},
            label = stringResource(com.dexter.app.R.string.login_password_label),
            passwordVisible = true,
            onToggleVisibility = {},
            modifier = Modifier
        )
    }
}
