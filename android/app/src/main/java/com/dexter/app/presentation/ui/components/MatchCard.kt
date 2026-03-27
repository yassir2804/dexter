package com.dexter.app.presentation.ui.components



import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import com.dexter.app.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import java.util.Date
import com.dexter.app.domain.model.MatchStatus
import com.dexter.app.domain.model.Match
import com.dexter.app.presentation.ui.theme.AppTheme

@Composable
fun MatchCard(
    match: Match,
    initials: String = "U",
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.Start) {
            AvatarCircle(initials = initials)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = stringResource(id = R.string.match_label, match.id), style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                val percent = (match.compatibilityScore * 100).toInt()
                Text(text = stringResource(id = R.string.match_compatibility_percent, percent), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MatchCardPreview() {
    val sample = Match(
        id = 42,
        initiatorId = 2,
        receiverId = 1,
        compatibilityScore = 0.88,
        status = MatchStatus.ACCEPTED,
        createdAt = Date()
    )
    AppTheme {
        MatchCard(match = sample, initials = "SF")
    }
}
