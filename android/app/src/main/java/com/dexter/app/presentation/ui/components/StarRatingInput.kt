package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.ui.theme.StarColor

/**
 * Interactive 5-star rating input.
 *
 * Taps on star N sets the rating to N (1..5). Filled stars use [StarColor];
 * empty stars use the M3 outline variant color.
 */
@Composable
fun StarRatingInput(
    rating: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Dp = 40.dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        (1..5).forEach { index ->
            val filled = index <= rating
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = stringResource(R.string.cd_star_rating, index),
                tint = if (filled) StarColor else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier
                    .size(starSize)
                    .clickable { onChange(index) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StarRatingInputEmptyPreview() {
    AppTheme(dynamicColor = false) {
        StarRatingInput(rating = 0, onChange = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun StarRatingInputThreePreview() {
    AppTheme(dynamicColor = false) {
        StarRatingInput(rating = 3, onChange = {})
    }
}
