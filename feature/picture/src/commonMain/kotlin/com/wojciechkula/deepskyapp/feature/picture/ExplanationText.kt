package com.wojciechkula.deepskyapp.feature.picture

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme

// Favourites saved from the legacy API hold plain text, newer ones HTML; the parser renders both.
@Composable
internal fun ExplanationText(
    explanation: String,
    modifier: Modifier = Modifier
) {
    val linkColor = ApodTheme.colors.primary
    val text = remember(explanation, linkColor) {
        explanationToAnnotatedString(
            html = explanation,
            linkStyles = TextLinkStyles(style = SpanStyle(color = linkColor, textDecoration = TextDecoration.Underline))
        )
    }
    Text(
        text = text,
        modifier = modifier,
        style = ApodTheme.typography.bodyMedium,
        color = ApodTheme.colors.onSurface,
        textAlign = TextAlign.Justify
    )
}

@LightDarkPreview
@Composable
private fun ExplanationTextPreview() = ApodColumnPreview {
    ExplanationText(
        explanation = "What does the <a href=\"https://example.com\">Andromeda galaxy</a> really look like? " +
            "The <i>featured</i> image shows it before processing &amp; cleanup."
    )
}
