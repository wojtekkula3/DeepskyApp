package com.wojciechkula.deepskyapp.core.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
actual fun rememberUrlOpener(): (String) -> Unit = remember {
    { url ->
        NSURL.URLWithString(url)?.let { target ->
            UIApplication.sharedApplication.openURL(target, options = emptyMap<Any?, Any>()) { }
        }
    }
}
