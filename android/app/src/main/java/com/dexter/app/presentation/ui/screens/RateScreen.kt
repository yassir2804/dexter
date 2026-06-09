package com.dexter.app.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import com.dexter.app.presentation.ui.components.PreviewSafeFlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dexter.app.R
import com.dexter.app.presentation.ui.components.AvatarCircle
import com.dexter.app.presentation.ui.components.DexterTextField
import com.dexter.app.presentation.ui.components.SkillChip
import com.dexter.app.presentation.ui.components.StarRatingInput
import com.dexter.app.presentation.ui.components.TopAppBarDexter
import com.dexter.app.presentation.ui.theme.AppTheme
import com.dexter.app.presentation.viewmodel.RateState
import com.dexter.app.presentation.viewmodel.RateViewModel

@Composable
fun RateScreen(
    sessionId: Long,
    viewModel: RateViewModel,
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(sessionId) {
        viewModel.loadTags(sessionId)
    }

    LaunchedEffect(state) {
        if (state is RateState.Submitted) onDoneClick()
    }

    RateContent(
        state = state,
        onBackClick = onBackClick,
        onRatingChange = viewModel::onRatingChange,
        onTagToggle = viewModel::onTagToggle,
        onCommentChange = viewModel::onCommentChange,
        onSubmit = viewModel::onSubmit,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RateContent(
    state: RateState,
    onBackClick: () -> Unit,
    onRatingChange: (Int) -> Unit,
    onTagToggle: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBarDexter(
                title = stringResource(R.string.rate_title),
                onBack = onBackClick
            )
        },
        modifier = modifier
    ) { padding ->
        when (state) {
            is RateState.Loading -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is RateState.Error -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is RateState.Editing -> {
                RateBody(
                    state = state,
                    onRatingChange = onRatingChange,
                    onTagToggle = onTagToggle,
                    onCommentChange = onCommentChange,
                    onSubmit = onSubmit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                )
            }
            RateState.Submitted -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RateBody(
    state: RateState.Editing,
    onRatingChange: (Int) -> Unit,
    onTagToggle: (String) -> Unit,
    onCommentChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Header card ─────────────────────────────────────────────
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                AvatarCircle(
                    initials = state.revieweeInitials,
                    size = 64.dp,
                    imageUrl = state.revieweeAvatarUrl
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.rate_session_with, state.sessionNumber, state.revieweeName),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // ── Rating ──────────────────────────────────────────────────
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.rate_how_was_it),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            StarRatingInput(rating = state.rating, onChange = onRatingChange)
            if (state.rating > 0) {
                Spacer(Modifier.height(4.dp))
                val labelRes = when (state.rating) {
                    1 -> R.string.rate_label_1
                    2 -> R.string.rate_label_2
                    3 -> R.string.rate_label_3
                    4 -> R.string.rate_label_4
                    else -> R.string.rate_label_5
                }
                Text(
                    text = stringResource(labelRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (state.showRatingError) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.rate_error_no_rating),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // ── Tags ────────────────────────────────────────────────────
        Column(Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.rate_tags_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            PreviewSafeFlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                state.tagCatalog.forEach { tag ->
                    SkillChip(
                        label = tag,
                        selected = state.selectedTags.contains(tag),
                        onClick = { onTagToggle(tag) }
                    )
                }
            }
        }

        // ── Comment ─────────────────────────────────────────────────
        DexterTextField(
            value = state.comment,
            onValueChange = onCommentChange,
            label = stringResource(R.string.rate_comment_label),
            singleLine = false,
            minLines = 3,
            maxLength = 500,
            contentDescriptionLabel = stringResource(R.string.cd_notes_input)
        )

        // ── Submit ──────────────────────────────────────────────────
        Button(
            onClick = onSubmit,
            enabled = !state.submitting,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50)
        ) {
            if (state.submitting) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    text = stringResource(R.string.rate_submit),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun RateSuccessPreview() {
    val catalog = listOf("Puntual", "Paciente", "Claro", "Motivador", "Preparado")
    AppTheme(dynamicColor = false) {
        RateContent(
            state = RateState.Editing(
                tagCatalog = catalog,
                rating = 4,
                selectedTags = setOf("Puntual", "Claro"),
                comment = "Excelente sesión, aprendí mucho.",
                submitting = false
            ),
            onBackClick = {},
            onRatingChange = {},
            onTagToggle = {},
            onCommentChange = {},
            onSubmit = {}
        )
    }
}
