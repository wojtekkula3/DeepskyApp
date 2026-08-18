package com.wojciechkula.deepskyapp

import androidx.compose.ui.window.ComposeUIViewController

// PascalCase on purpose: Swift calls this as `MainViewControllerKt.MainViewController()`, and the name
// is the Compose Multiplatform convention for the iOS entry point.
@Suppress("ktlint:standard:function-naming", "FunctionNaming")
fun MainViewController() = ComposeUIViewController { App() }
