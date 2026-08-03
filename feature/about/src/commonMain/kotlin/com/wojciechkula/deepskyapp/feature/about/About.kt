package com.wojciechkula.deepskyapp.feature.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wojciechkula.deepskyapp.core.designsystem.icon.DeepskyContentDescriptions
import com.wojciechkula.deepskyapp.core.designsystem.icon.DeepskyIcons
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import com.wojciechkula.deepskyapp.feature.about.resources.Res
import com.wojciechkula.deepskyapp.feature.about.resources.about_body
import com.wojciechkula.deepskyapp.feature.about.resources.about_content_description_apod_logo
import com.wojciechkula.deepskyapp.feature.about.resources.about_content_description_nasa_logo
import com.wojciechkula.deepskyapp.feature.about.resources.about_powered_by
import com.wojciechkula.deepskyapp.feature.about.resources.about_title
import com.wojciechkula.deepskyapp.feature.about.resources.apod_logo
import com.wojciechkula.deepskyapp.feature.about.resources.nasa_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun About(onBack: () -> Unit) {
    AboutScreen(onBack = onBack)
}

@Composable
private fun AboutScreen(onBack: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            ) {
                Icon(
                    painter = DeepskyIcons.back(),
                    contentDescription = DeepskyContentDescriptions.back
                )
            }
            Text(
                text = stringResource(Res.string.about_title),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            Text(
                text = stringResource(Res.string.about_powered_by),
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.nasa_logo),
                    contentDescription = stringResource(Res.string.about_content_description_nasa_logo),
                    modifier = Modifier.size(160.dp)
                )
                Image(
                    painter = painterResource(Res.drawable.apod_logo),
                    contentDescription = stringResource(Res.string.about_content_description_apod_logo),
                    modifier = Modifier.size(120.dp)
                )
            }
            Text(
                text = stringResource(Res.string.about_body),
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(all = 18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun AboutPreview() {
    DeepskyTheme {
        AboutScreen(onBack = {})
    }
}
