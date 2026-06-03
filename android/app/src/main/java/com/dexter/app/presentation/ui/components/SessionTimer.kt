package com.dexter.app.presentation.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.dexter.app.presentation.ui.theme.AppTheme
import kotlinx.coroutines.delay
import java.util.Date

/**
 * Live timer that ticks every second and renders mm:ss (or hh:mm:ss
 * when the elapsed time exceeds one hour).
 *
 * The elapsed time is derived from [startedAt] so the value survives
 * recomposition and stays consistent even if the screen is paused/resumed.
 */
@Composable
fun SessionTimer(
    startedAt: Date,
    modifier: Modifier = Modifier
) {
    var nowMs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(startedAt) {
        while (true) {
            nowMs = System.currentTimeMillis()
            delay(1000L)
        }
    }

    val elapsedSec = ((nowMs - startedAt.time) / 1000L).coerceAtLeast(0L)
    val h = elapsedSec / 3600L
    val m = (elapsedSec % 3600L) / 60L
    val s = elapsedSec % 60L
    val label = if (h > 0) String.format("%02d:%02d:%02d", h, m, s)
    else String.format("%02d:%02d", m, s)

    Text(
        text = label,
        style = MaterialTheme.typography.displayMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
private fun SessionTimerPreview() {
    AppTheme(dynamicColor = false) {
        SessionTimer(startedAt = Date(System.currentTimeMillis() - 125_000L))
    }
}
