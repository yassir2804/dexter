package com.dexter.app.presentation.ui.components



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun SkillChip(
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val bg = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Text(
        text = label,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        style = MaterialTheme.typography.labelMedium
    )
}

@Preview(showBackground = true, name = "SkillChip - Selected")
@Composable
fun SkillChipSelectedPreview() {
    MaterialTheme {
        SkillChip(label = "Guitarra", selected = true)
    }
}

@Preview(showBackground = true, name = "SkillChip - Unselected")
@Composable
fun SkillChipUnselectedPreview() {
    MaterialTheme {
        SkillChip(label = "Inglés", selected = false)
    }
}
