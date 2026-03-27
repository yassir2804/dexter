package com.dexter.app.presentation.ui.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.presentation.navigation.NavRoutes
import com.dexter.app.presentation.ui.theme.AppTheme

/**
 * MainLayout: applies paddingValues and shows a contextual header
 * depending on which main tab is active.
 */
@Composable
fun MainLayout(
    paddingValues: PaddingValues,
    currentRoute: String? = null,
    userName: String = "",
    onSettingsClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (currentRoute) {
                NavRoutes.Home.ROUTE -> {
                    if (userName.isNotBlank()) {
                        Text(
                            text = stringResource(R.string.home_greeting, userName),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        text = stringResource(R.string.home_headline),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                NavRoutes.Search.ROUTE -> {
                    Text(
                        text = stringResource(R.string.search_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                NavRoutes.Messages.ROUTE -> {
                    Text(
                        text = stringResource(R.string.messages_title),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                NavRoutes.Profile.ROUTE -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.profile_title),
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onSettingsClick) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = stringResource(R.string.cd_more_options)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainLayoutHomePreview() {
    AppTheme(dynamicColor = false) {
        MainLayout(
            paddingValues = PaddingValues(),
            currentRoute = NavRoutes.Home.ROUTE,
            userName = "Alice"
        ) {
            Text("Home content")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainLayoutSearchPreview() {
    AppTheme(dynamicColor = false) {
        MainLayout(
            paddingValues = PaddingValues(),
            currentRoute = NavRoutes.Search.ROUTE
        ) {
            Text("Search content")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainLayoutMessagesPreview() {
    AppTheme(dynamicColor = false) {
        MainLayout(
            paddingValues = PaddingValues(),
            currentRoute = NavRoutes.Messages.ROUTE
        ) {
            Text("Messages content")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainLayoutProfilePreview() {
    AppTheme(dynamicColor = false) {
        MainLayout(
            paddingValues = PaddingValues(),
            currentRoute = NavRoutes.Profile.ROUTE
        ) {
            Text("Profile content (own header)")
        }
    }
}
