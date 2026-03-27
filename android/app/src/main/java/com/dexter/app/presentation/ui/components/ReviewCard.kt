package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.ui.theme.StarColor


@Composable
fun ReviewCard(
    reviewerName: String,
    reviewerInitials: String = "",
    reviewerImageUrl: String? = null,
    rating: Int,
    comment: String,
    modifier: Modifier = Modifier
) {
    val starsDescription = stringResource(R.string.cd_star_rating, rating)
    val initials = reviewerInitials.ifBlank {
        reviewerName.split(" ").mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
    }

    ElevatedCard(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            AvatarCircle(
                initials = initials,
                size = 40.dp,
                imageUrl = reviewerImageUrl
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = reviewerName,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        modifier = Modifier.semantics { contentDescription = starsDescription }
                    ) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < rating) StarColor
                                       else MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = comment,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewCardPreview() {
    AppTheme(dynamicColor = false) {
        ReviewCard(
            reviewerName = "Bob Smith",
            rating = 5,
            comment = "Excelente profesora de guitarra. Muy paciente y metodica."
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewCardWithPhotoPreview() {
    AppTheme(dynamicColor = false) {
        ReviewCard(
            reviewerName = "Bob Smith",
            reviewerImageUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150",
            rating = 4,
            comment = "Buenas sesiones de ingles. Explica con ejemplos practicos."
        )
    }
}
