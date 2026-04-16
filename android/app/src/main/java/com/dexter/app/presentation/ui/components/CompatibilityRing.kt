package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dexter.app.presentation.ui.theme.AppTheme

/**
 * Circular progress ring rendering a compatibility [percentage] 0..100.
 *
 * Background stroke uses [MaterialTheme.colorScheme.surfaceVariant];
 * the progress arc uses primary. The percentage is displayed at the center.
 */
@Composable
fun CompatibilityRing(
    percentage: Int,
    modifier: Modifier = Modifier,
    size: Dp = 110.dp,
    strokeWidth: Dp = 10.dp
) {
    val clamped = percentage.coerceIn(0, 100)
    val sweep = 360f * clamped / 100f
    val bgColor = MaterialTheme.colorScheme.surfaceVariant
    val fgColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx())
            val arcSize = Size(
                this.size.width - strokeWidth.toPx(),
                this.size.height - strokeWidth.toPx()
            )
            val topLeft = Offset(strokeWidth.toPx() / 2f, strokeWidth.toPx() / 2f)

            drawArc(
                color = bgColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
            drawArc(
                color = fgColor,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
        }
        Text(
            text = "$clamped%",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CompatibilityRingPreview() {
    AppTheme(dynamicColor = false) {
        CompatibilityRing(percentage = 95)
    }
}

@Preview(showBackground = true)
@Composable
private fun CompatibilityRingLowPreview() {
    AppTheme(dynamicColor = false) {
        CompatibilityRing(percentage = 40)
    }
}
