package com.wojciechkula.deepskyapp.core.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.flow.first

/**
 * Reports [viewModel]'s screen as shown once per composition, when its lifecycle first reaches RESUMED,
 * and as hidden when it leaves composition. Returning from the background keeps the composition, so it
 * reports nothing.
 */
@Composable
fun TrackScreen(viewModel: StateActionsViewModel<*, *>) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    // Not on entering composition: a predictive back gesture composes the previous screen while only STARTED,
    // and cancelling it would log a screen the user never returned to.
    LaunchedEffect(viewModel, lifecycle) {
        lifecycle.currentStateFlow.first { it.isAtLeast(Lifecycle.State.RESUMED) }
        viewModel.onScreenDisplayed()
    }
    DisposableEffect(viewModel) {
        onDispose { viewModel.onScreenHidden() }
    }
}
