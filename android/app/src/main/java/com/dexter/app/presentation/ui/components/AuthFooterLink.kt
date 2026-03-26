package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.withStyle
import com.dexter.app.presentation.ui.theme.AppTheme

/**
 * Inline footer link used across auth screens.
 * @param prefixText text shown before the link (can be empty)
 * @param linkText clickable link text
 */
@Composable
fun AuthFooterLink(
    prefixText: String,
    linkText: String,
    onClick: () -> Unit,
    contentDescriptionLabel: String,
    modifier: Modifier = Modifier
) {
    val annotated = buildAnnotatedString {
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) {
            append(prefixText)
            if (prefixText.isNotEmpty()) append(" ")
        }
        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)) {
            append(linkText)
        }
    }

    Text(
        text = annotated,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        modifier = modifier
            .semantics { contentDescription = contentDescriptionLabel }
            .clickable(onClick = onClick)
    )
}

@Preview(showBackground = true)
@Composable
private fun AuthFooterLinkPreviewWithPrefix() {
    AppTheme(dynamicColor = false) {
        AuthFooterLink(
            prefixText = stringResource(com.dexter.app.R.string.login_no_account),
            linkText = stringResource(com.dexter.app.R.string.login_register_link),
            onClick = {},
            contentDescriptionLabel = stringResource(com.dexter.app.R.string.cd_register_link),
            modifier = Modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AuthFooterLinkPreviewOnlyLink() {
    AppTheme(dynamicColor = false) {
        AuthFooterLink(
            prefixText = "",
            linkText = stringResource(com.dexter.app.R.string.forgot_back_login),
            onClick = {},
            contentDescriptionLabel = stringResource(com.dexter.app.R.string.cd_back_to_login),
            modifier = Modifier
        )
    }
}
