package com.wojciechkula.deepskyapp.core.mvvm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.Flow

/**
 * Collects one-shot [actions] emitted by a [StateActionsViewModel] and forwards each to [onAction]
 * (typically mapped to navigation or transient UI like a snackbar).
 *
 * [onAction] must not block: it is invoked while collecting, so a suspending call inside it would
 * delay every following action. Launch long-running UI work (for example `showSnackbar`) into a
 * `rememberCoroutineScope()` instead.
 */
@Composable
fun <Action> ActionsEffect(actions: Flow<Action>, onAction: (Action) -> Unit) {
    val currentOnAction by rememberUpdatedState(onAction)
    LaunchedEffect(actions) {
        actions.collect { currentOnAction(it) }
    }
}
