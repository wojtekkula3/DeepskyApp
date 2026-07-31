package com.wojciechkula.deepskyapp

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDay

// TEMPORARY (Milestone 4): renders Picture of the Day directly to verify the feature end-to-end.
// Replaced by the Navigation3 NavDisplay host + bottom navigation in Milestone 5.
@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .crossfade(true)
            .build()
    }
    DeepskyTheme {
        PictureOfTheDay()
    }
}
