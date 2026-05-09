package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Safe wrapper for chip rows.
 *
 * Some runtime/device combinations can crash with NoSuchMethodError on
 * FlowLayoutKt.FlowRow due to Compose binary mismatch. To keep the app stable,
 * this implementation always uses a horizontally scrollable Row.
 */
@Composable
fun PreviewSafeFlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = horizontalArrangement
    ) { content() }
}
