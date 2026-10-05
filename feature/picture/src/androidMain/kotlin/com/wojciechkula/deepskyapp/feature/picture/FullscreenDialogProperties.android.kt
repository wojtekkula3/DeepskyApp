package com.wojciechkula.deepskyapp.feature.picture

import androidx.compose.ui.window.DialogProperties

internal actual fun fullscreenDialogProperties() = DialogProperties(
    usePlatformDefaultWidth = false,
    dismissOnClickOutside = false,
    decorFitsSystemWindows = false
)
