package com.wojciechkula.deepskyapp.core.mvvm

/**
 * Maps a screen's state to its `screen_view` name and sends it when that name changes. Passed to
 * [StateActionsViewModel], which feeds it every state; the screen reports being shown and hidden through
 * [TrackScreen], so returning to it logs again and a change made while it is hidden is not logged twice.
 *
 * A screen's custom events belong in its handler too, so all of its analytics lives in one file.
 */
abstract class AnalyticsStateHandler<State> {
    private var lastSentScreenName: String? = null
    private var awaitingFirstDisplay = true
    private var isHidden = false

    /** The `screen_name` for [state], or null for a state that logs nothing (a transient loader). */
    abstract fun mapStateToScreenName(state: State): String?

    abstract fun sendScreenView(screenName: String)

    internal fun onStateChanged(state: State) {
        if (isHidden) return
        val screenName = mapStateToScreenName(state) ?: return
        if (screenName == lastSentScreenName) return
        lastSentScreenName = screenName
        sendScreenView(screenName)
    }

    // The first display after entry is skipped when onStateChanged has already logged it, or it would count twice.
    internal fun onScreenDisplayed(state: State) {
        isHidden = false
        if (awaitingFirstDisplay && lastSentScreenName != null) {
            awaitingFirstDisplay = false
            return
        }
        awaitingFirstDisplay = false
        lastSentScreenName = null
        onStateChanged(state)
    }

    // A ViewModel under another screen still changes state; that change is logged once the screen is back.
    internal fun onScreenHidden() {
        isHidden = true
    }
}
