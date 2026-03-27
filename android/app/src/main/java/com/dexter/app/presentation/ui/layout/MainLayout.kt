package com.dexter.app.presentation.ui.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.presentation.ui.theme.AppTheme
import androidx.compose.material3.Text

/**
 * Minimal MainLayout: aplica `paddingValues` y renderiza `content`.
 * Diseñado para ser usado desde un `Scaffold` en `MainScreen`.
 */
@Composable
fun MainLayout(paddingValues: PaddingValues, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainLayoutPreview() {
    AppTheme(dynamicColor = false) {
        MainLayout(paddingValues = PaddingValues()) {
            Text("Main content")
        }
    }
}

