package com.wojciechkula.deepskyapp

import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import com.wojciechkula.deepskyapp.navigation.DeepskyNavHost

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components { add(KtorNetworkFetcherFactory()) }
            .crossfade(true)
            .build()
    }
    DeepskyTheme {
        DeepskyNavHost()
    }
}
