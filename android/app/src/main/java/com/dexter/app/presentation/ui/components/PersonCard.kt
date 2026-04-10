package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dexter.app.R
import com.dexter.app.domain.model.PersonResult
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.ui.theme.StarColor

/**
 * Card for a person in search results.
 * Shows avatar, name, skill, rating, availability, and optional AI badge.
 */
@Composable
fun PersonCard(
    person: PersonResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardDescription = stringResource(R.string.cd_person_card, person.name)

    Box(modifier = modifier.fillMaxWidth()) {
        ElevatedCard(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = cardDescription }
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                AvatarCircle(
                    initials = person.initials,
                    imageUrl = person.avatarUrl,
                    size = 64.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = person.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = person.skillDescription,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = StarColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = person.rating.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.search_sessions_count, person.sessionCount),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Availability pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                if (person.aiScore >= 90)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = if (person.aiScore >= 90)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = person.availability,
                            style = MaterialTheme.typography.titleSmall,
                            fontSize = 11.sp,
                            color = if (person.aiScore >= 90)
                                MaterialTheme.colorScheme.onPrimaryContainer
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // AI reason text
                    if (person.aiScore >= 90) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = person.aiReason,
                                style = MaterialTheme.typography.titleSmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 4.dp)
                )
            }
        }

        // AI score badge (floating top-right)
        if (person.aiScore >= 90) {
            val badgeDescription = stringResource(R.string.cd_ai_badge, person.aiScore)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-12).dp, y = (-6).dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .semantics { contentDescription = badgeDescription }
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.search_ai_match, person.aiScore),
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PersonCardWithAiBadgePreview() {
    AppTheme(dynamicColor = false) {
        PersonCard(
            person = PersonResult(
                userId = 3L,
                name = "Sofia Rodriguez",
                avatarUrl = null,
                initials = "SR",
                skillDescription = "Guitarra clasica \u00b7 5 a\u00f1os",
                rating = 4.9,
                sessionCount = 47,
                availability = "Lun-Mie 18:00-20:00",
                aiScore = 98,
                aiReason = "Coincide 100% con tu disponibilidad"
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PersonCardWithoutAiBadgePreview() {
    AppTheme(dynamicColor = false) {
        PersonCard(
            person = PersonResult(
                userId = 5L,
                name = "Ana Martinez",
                avatarUrl = null,
                initials = "AM",
                skillDescription = "Guitarra acustica \u00b7 3 a\u00f1os",
                rating = 4.7,
                sessionCount = 28,
                availability = "Vie-Sab 10:00-14:00",
                aiScore = 72,
                aiReason = "Disponibilidad parcial"
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
