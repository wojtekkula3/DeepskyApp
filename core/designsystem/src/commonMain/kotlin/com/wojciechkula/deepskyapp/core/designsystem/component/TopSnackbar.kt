package com.wojciechkula.deepskyapp.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import kotlinx.coroutines.delay

/**
 * A snackbar shown at the top of the screen, driven by state rather than by a one-shot channel:
 * the screen keeps the message in its UiState and clears it from [onDismiss].
 */
@Composable
fun TopSnackbar(
    modifier: Modifier = Modifier,
    message: String,
    type: TopSnackbarType = TopSnackbarType.SUCCESS,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(width = 1.dp, color = type.borderColor)
    ) {
        Row {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            )
            Box(
                modifier = Modifier
                    .size(CloseButtonSize)
                    .clickable(onClick = onDismiss),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✕",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Renders [TopSnackbar] and dismisses it automatically after [SnackbarDuration]. Host it in a Box
 * aligned to the top of the screen, above the content.
 */
@Composable
fun TopSnackbarHost(
    modifier: Modifier = Modifier,
    message: String,
    type: TopSnackbarType = TopSnackbarType.SUCCESS,
    onDismiss: () -> Unit
) {
    LaunchedEffect(message) {
        delay(SnackbarDuration)
        onDismiss()
    }
    TopSnackbar(
        modifier = modifier,
        message = message,
        type = type,
        onDismiss = onDismiss
    )
}

enum class TopSnackbarType {
    SUCCESS,
    ERROR;

    internal val borderColor: Color
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            SUCCESS -> MaterialTheme.colorScheme.primary
            ERROR -> MaterialTheme.colorScheme.error
        }
}

private val CloseButtonSize = 40.dp
private const val SnackbarDuration = 3_000L

@Preview
@Composable
private fun TopSnackbarSuccessPreview() {
    DeepskyTheme {
        TopSnackbar(
            message = "Picture saved to favourites",
            type = TopSnackbarType.SUCCESS,
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun TopSnackbarErrorPreview() {
    DeepskyTheme {
        TopSnackbar(
            message = "Error while deleting the picture",
            type = TopSnackbarType.ERROR,
            onDismiss = {}
        )
    }
}
