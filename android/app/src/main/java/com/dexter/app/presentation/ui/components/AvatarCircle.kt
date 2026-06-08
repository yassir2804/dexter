package com.dexter.app.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.util.ImageUtils

@Composable
fun AvatarCircle(
    initials: String,
    size: Dp = 40.dp,
    imageUrl: String? = null,
    modifier: Modifier = Modifier
) {
    val isBase64 = imageUrl?.startsWith("data:") == true
    val bitmap = remember(imageUrl) {
        if (isBase64 && imageUrl != null) ImageUtils.base64ToBitmap(imageUrl) else null
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (!imageUrl.isNullOrBlank()) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = initials.take(2).uppercase(),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = (size.value * 0.35).sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AvatarCircleInitialsPreview() {
    AppTheme(dynamicColor = false) {
        AvatarCircle(initials = "AG", size = 80.dp)
    }
}

@Preview(showBackground = true)
@Composable
private fun AvatarCircleSmallPreview() {
    AppTheme(dynamicColor = false) {
        AvatarCircle(initials = "BS", size = 40.dp)
    }
}
