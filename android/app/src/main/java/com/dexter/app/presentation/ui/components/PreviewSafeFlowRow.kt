package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode

/**
 * FlowRow wrapper that avoids a NoSuchMethodError on FlowRow#FlowRowOverflow
 * when Android Studio's preview renderer (layoutlib) ships with an older
 * Compose foundation than the project's compile classpath.
 *
 * At runtime uses the real FlowRow (wraps chips across lines).
 * In preview mode falls back to a horizontally-scrollable Row — chips may
 * not wrap visually, but the render does not fail.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PreviewSafeFlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable () -> Unit
) {
    if (LocalInspectionMode.current) {
        Row(
            modifier = modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = horizontalArrangement
        ) { content() }
    } else {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = horizontalArrangement
        ) { content() }
    }
}
