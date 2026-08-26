package com.wojciechkula.deepskyapp.feature.picture

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme

/** A bold label above its value, shared by the Picture of the Day and Picture Details description boxes. */
@Composable
internal fun LabelledText(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(modifier = modifier.padding(top = 8.dp)) {
        Text(label, fontWeight = FontWeight.Bold, style = ApodTheme.typography.bodyLarge)
        Text(value, style = ApodTheme.typography.bodyMedium)
    }
}
