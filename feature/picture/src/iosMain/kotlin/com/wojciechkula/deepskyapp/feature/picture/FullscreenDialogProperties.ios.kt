package com.wojciechkula.deepskyapp.feature.picture

import androidx.compose.ui.window.DialogProperties

// With the default platform insets the content stops at the safe area, and the previous screen shows
// through the dialog's translucent scrim under the status bar and the home indicator.
internal actual fun fullscreenDialogProperties() = DialogProperties(
    usePlatformDefaultWidth = false,
    dismissOnClickOutside = false,
    usePlatformInsets = false
)
