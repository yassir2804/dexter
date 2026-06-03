package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.presentation.ui.theme.AppTheme

/**
 * Step indicator dots for multi-step flows (e.g. registration).
 *
 * @param totalSteps Total number of steps
 * @param currentStep Current active step (0-indexed)
 */
@Composable
fun StepDots(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        repeat(totalSteps) { index ->
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentStep) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StepDotsStep1Preview() {
    AppTheme(dynamicColor = false) {
        StepDots(totalSteps = 3, currentStep = 0)
    }
}

@Preview(showBackground = true)
@Composable
private fun StepDotsStep2Preview() {
    AppTheme(dynamicColor = false) {
        StepDots(totalSteps = 3, currentStep = 1)
    }
}
