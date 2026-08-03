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
import com.wojciechkula.deepskyapp.core.designsystem.icon.DeepskyIcons
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import com.wojciechkula.deepskyapp.feature.about.resources.Res
import com.wojciechkula.deepskyapp.feature.about.resources.apod_logo
import com.wojciechkula.deepskyapp.feature.about.resources.nasa_logo
import org.jetbrains.compose.resources.painterResource

// Copied verbatim from the original fragment_about.xml.
private const val ABOUT_TEXT =
    "Welcome people - thirsty of night sky travelers! This application will help you discover the " +
        "wonders of our Universe!\n\nEach day, at 00:00 GMT-4 time, the new picture is served to " +
        "the main screen. From that moment it is available to view for the 24h, after this time " +
        "new picture will be shown. You can also save it for later if you liked it.\n\nAstronomy " +
        "Picture of the Day (APOD) is originated, written, coordinated, and edited since 1995 by " +
        "Robert Nemiroff and Jerry Bonnell. The APOD archive contains the largest collection of " +
        "annotated astronomical images on the internet. In real life, Robert and Jerry are two " +
        "professional astronomers who spend most of their time researching the universe. Robert " +
        "is a professor at Michigan Technological University in Houghton, Michigan, USA, while " +
        "Jerry is a scientist at NASA's Goddard Space Flight Center in Greenbelt, Maryland USA."

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
                Icon(painter = DeepskyIcons.back(), contentDescription = "Back")
            }
            Text(
                text = "Deepsky App",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            )
            Text(
                text = "POWERED BY",
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
                    contentDescription = "NASA logo",
                    modifier = Modifier.size(160.dp)
                )
                Image(
                    painter = painterResource(Res.drawable.apod_logo),
                    contentDescription = "Astronomy Picture of the Day logo",
                    modifier = Modifier.size(120.dp)
                )
            }
            Text(
                text = ABOUT_TEXT,
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
