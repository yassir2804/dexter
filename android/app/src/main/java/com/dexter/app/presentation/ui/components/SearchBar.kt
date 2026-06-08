package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.presentation.ui.theme.AppTheme

@Composable
fun SearchBar(
    query: String? = null,
    onQueryChanged: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val (internalQuery, setInternalQuery) = remember { mutableStateOf("") }
    val displayQuery = query ?: internalQuery

    TextField(
        value = displayQuery,
        onValueChange = { value ->
            if (value.length <= 100) {
                if (query == null) setInternalQuery(value)
                onQueryChanged(value)
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        placeholder = {
            Text(
                text = stringResource(R.string.home_search_placeholder),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .height(56.dp)
            .shadow(2.dp, RoundedCornerShape(50))
            .clip(RoundedCornerShape(50)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchBarPreview() {
    AppTheme(dynamicColor = false) {
        SearchBar()
    }
}
