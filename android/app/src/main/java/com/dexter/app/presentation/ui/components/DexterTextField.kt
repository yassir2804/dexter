package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dexter.app.presentation.ui.theme.AppTheme

/**
 * Reusable text field following Dexter's M3 styles.
 * @param maxLength maximum characters allowed (enforced via onValueChange filter)
 */
@Composable
fun DexterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLength: Int = Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    supportingText: String? = null,
    contentDescriptionLabel: String,
    modifier: Modifier = Modifier
) {
    // Envolvemos campo + texto de apoyo en una Column para que NUNCA se solapen,
    // sin importar si el contenedor padre es un Box, Row o Column.
    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = { if (it.length <= maxLength) onValueChange(it) },
            label = { Text(label) },
            placeholder = if (placeholder != null) ({ Text(placeholder) }) else null,
            leadingIcon = if (leadingIcon != null) {
                { androidx.compose.material3.Icon(imageVector = leadingIcon, contentDescription = null, modifier = Modifier.size(20.dp)) }
            } else null,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            isError = isError,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = contentDescriptionLabel }
        )

        if (!supportingText.isNullOrEmpty()) {
            Text(
                text = supportingText,
                color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DexterTextFieldPreviewSimple() {
    AppTheme(dynamicColor = false) {
        DexterTextField(
            value = "",
            onValueChange = {},
            label = stringResource(com.dexter.app.R.string.login_email_label),
            placeholder = stringResource(com.dexter.app.R.string.login_email_placeholder),
            contentDescriptionLabel = stringResource(com.dexter.app.R.string.cd_email_input),
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DexterTextFieldPreviewWithIcon() {
    AppTheme(dynamicColor = false) {
        DexterTextField(
            value = "",
            onValueChange = {},
            label = stringResource(com.dexter.app.R.string.login_email_label),
            placeholder = stringResource(com.dexter.app.R.string.login_email_placeholder),
            leadingIcon = Icons.Default.Email,
            contentDescriptionLabel = stringResource(com.dexter.app.R.string.cd_email_input),
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DexterTextFieldPreviewMultiline() {
    AppTheme(dynamicColor = false) {
        DexterTextField(
            value = "Linea1\nLinea2",
            onValueChange = {},
            label = stringResource(com.dexter.app.R.string.rate_comment_label),
            placeholder = stringResource(com.dexter.app.R.string.active_session_notes_placeholder),
            singleLine = false,
            minLines = 2,
            contentDescriptionLabel = stringResource(com.dexter.app.R.string.cd_notes_input),
            modifier = Modifier
        )
    }
}
