package com.dexter.app.presentation.ui.components



import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.dexter.app.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    placeholder: String = "Search",
    onQueryChanged: (String) -> Unit = {}
) {
    val (query, setQuery) = remember { mutableStateOf("") }

    val ph = stringResource(id = R.string.home_search_placeholder)
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)) {
        TextField(
            value = query,
            onValueChange = { value -> setQuery(value); onQueryChanged(value) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            placeholder = { Text(text = ph) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    MaterialTheme {
        SearchBar(placeholder = "Buscar habilidad…")
    }
}
