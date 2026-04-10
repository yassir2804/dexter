package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.domain.model.SkillIconType
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.ui.theme.SkillCameraBg
import com.dexter.app.presentation.ui.theme.SkillCameraFg
import com.dexter.app.presentation.ui.theme.SkillCodeBg
import com.dexter.app.presentation.ui.theme.SkillCodeFg
import com.dexter.app.presentation.ui.theme.SkillDesignBg
import com.dexter.app.presentation.ui.theme.SkillDesignFg
import com.dexter.app.presentation.ui.theme.SkillLanguageBg
import com.dexter.app.presentation.ui.theme.SkillLanguageFg
import com.dexter.app.presentation.ui.theme.SkillMarketingBg
import com.dexter.app.presentation.ui.theme.SkillMarketingFg

/**
 * A list item for a popular skill in the search screen.
 * Shows a colored icon, skill name, people count, and a chevron.
 */
@Composable
fun SkillListItem(
    skillName: String,
    peopleCount: Int,
    iconType: SkillIconType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bgColor, fgColor, icon) = getSkillIconProps(iconType)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(40.dp)
                .background(bgColor, RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fgColor,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = skillName,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = stringResource(R.string.search_people_count, peopleCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

private data class SkillIconProps(val bg: Color, val fg: Color, val icon: ImageVector)

@Composable
private fun getSkillIconProps(iconType: SkillIconType): SkillIconProps {
    return when (iconType) {
        SkillIconType.MUSIC -> SkillIconProps(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            Icons.Default.MusicNote
        )
        SkillIconType.LANGUAGE -> SkillIconProps(
            SkillLanguageBg, SkillLanguageFg, Icons.Default.Translate
        )
        SkillIconType.CODE -> SkillIconProps(
            SkillCodeBg, SkillCodeFg, Icons.Default.Code
        )
        SkillIconType.CAMERA -> SkillIconProps(
            SkillCameraBg, SkillCameraFg, Icons.Default.CameraAlt
        )
        SkillIconType.DESIGN -> SkillIconProps(
            SkillDesignBg, SkillDesignFg, Icons.Default.Brush
        )
        SkillIconType.MARKETING -> SkillIconProps(
            SkillMarketingBg, SkillMarketingFg, Icons.Default.TrendingUp
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SkillListItemPreview() {
    AppTheme(dynamicColor = false) {
        SkillListItem(
            skillName = "Guitarra",
            peopleCount = 12,
            iconType = SkillIconType.MUSIC,
            onClick = {}
        )
    }
}
