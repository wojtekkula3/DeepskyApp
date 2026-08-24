package com.wojciechkula.deepskyapp.core.media

import androidx.compose.runtime.Composable

/**
 * Returns a callback that hands a URL to whichever platform browser or app claims it.
 *
 * A composable rather than an injected interface: opening a link is platform navigation, not business
 * logic, and the screens already take navigation as lambdas.
 */
@Composable
expect fun rememberUrlOpener(): (String) -> Unit
