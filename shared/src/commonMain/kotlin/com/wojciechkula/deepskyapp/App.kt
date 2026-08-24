package com.wojciechkula.deepskyapp

import androidx.compose.runtime.Composable
import coil3.ComponentRegistry
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import com.wojciechkula.deepskyapp.core.media.addVideoFrameFetcher
import com.wojciechkula.deepskyapp.core.media.videoFrameLoader
import com.wojciechkula.deepskyapp.domain.model.isVideoFileUrl
import com.wojciechkula.deepskyapp.navigation.DeepskyNavHost

/**
 * The app's Coil components, in one registry and in this order: Coil takes the first registered fetcher
 * that claims the URI, and the network one claims every https URL, so the video fetcher has to precede
 * it. A second `components { }` call would replace this registry rather than add to it.
 *
 * Extracted from [App] so the order can be asserted without a `PlatformContext`.
 */
internal fun ComponentRegistry.Builder.deepskyComponents(): ComponentRegistry.Builder {
    addVideoFrameFetcher(::isVideoFileUrl, videoFrameLoader())
    return add(KtorNetworkFetcherFactory())
}

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { deepskyComponents() }
            .crossfade(true)
            .build()
    }
    DeepskyTheme {
        DeepskyNavHost()
    }
}
