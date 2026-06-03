package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.presentation.ui.theme.AppTheme

/**
 * Skill chip with 8dp radius (M3 spec, NOT pill).
 *
 * When selected, shows a check icon and a border to differentiate from unselected.
 * When isError, uses errorContainer colors (for "sought" skills).
 */
@Composable
fun SkillChip(
    label: String,
    selected: Boolean = false,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val isActive = selected || isError
    val shape = RoundedCornerShape(8.dp)

    val bg = when {
        isError -> MaterialTheme.colorScheme.errorContainer
        selected -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = when {
        isError -> MaterialTheme.colorScheme.onErrorContainer
        selected -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val borderColor = when {
        isError -> MaterialTheme.colorScheme.error
        selected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .clip(shape)
            .border(
                width = if (isActive) 1.dp else 0.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(onClick = onClick)
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (isActive) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
        }
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Preview(showBackground = true, name = "SkillChip - Selected")
@Composable
fun SkillChipSelectedPreview() {
    AppTheme(dynamicColor = false) {
        SkillChip(label = "Guitarra", selected = true)
    }
}

@Preview(showBackground = true, name = "SkillChip - Unselected")
@Composable
fun SkillChipUnselectedPreview() {
    AppTheme(dynamicColor = false) {
        SkillChip(label = "Inglés", selected = false)
    }
}

@Preview(showBackground = true, name = "SkillChip - Error (Sought)")
@Composable
fun SkillChipErrorPreview() {
    AppTheme(dynamicColor = false) {
        SkillChip(label = "Fotografía", isError = true)
    }
}
