package com.wojciechkula.deepskyapp.core.mvvm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Base ViewModel for the MVI-style screens: holds immutable [State] exposed as [states] and emits
 * one-shot [Action]s (navigation / side effects) via [actions]. Screens send user intents through a
 * screen-specific `handleUiEvent` function declared by each subclass.
 *
 * Use [Nothing] as [Action] for screens that have no one-shot actions.
 */
abstract class StateActionsViewModel<State, Action>(initialState: State) : ViewModel() {

    private val _states = MutableStateFlow(initialState)
    val states: StateFlow<State> = _states.asStateFlow()

    private val _actions = Channel<Action>(Channel.BUFFERED)
    val actions: Flow<Action> = _actions.receiveAsFlow()

    protected val currentState: State get() = _states.value

    protected fun updateState(reducer: State.() -> State) {
        _states.update(reducer)
    }

    protected fun action(action: Action) {
        viewModelScope.launch { _actions.send(action) }
    }

    protected fun launch(block: suspend CoroutineScope.() -> Unit): Job =
        viewModelScope.launch(block = block)
}
