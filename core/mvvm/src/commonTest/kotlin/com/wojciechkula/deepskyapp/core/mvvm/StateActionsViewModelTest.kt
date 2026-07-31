package com.wojciechkula.deepskyapp.core.mvvm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private data class TestState(val counter: Int = 0, val label: String = "")

private sealed interface TestAction {
    data object Closed : TestAction
    data class Opened(val id: Int) : TestAction
}

private class TestViewModel : StateActionsViewModel<TestState, TestAction>(TestState()) {
    fun increment() = updateState { copy(counter = counter + 1) }
    fun setLabel(value: String) = updateState { copy(label = value) }
    fun readCounter(): Int = currentState.counter
    fun emit(action: TestAction) = action(action)
    fun incrementInBackground(times: Int) {
        repeat(times) { launch { updateState { copy(counter = counter + 1) } } }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class StateActionsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun statesStartWithTheInitialState() = runTest(dispatcher) {
        val viewModel = TestViewModel()

        assertEquals(TestState(), viewModel.states.value)
    }

    @Test
    fun updateStateAppliesTheReducerAndIsVisibleAsCurrentState() = runTest(dispatcher) {
        val viewModel = TestViewModel()

        viewModel.increment()
        viewModel.setLabel("ready")

        assertEquals(TestState(counter = 1, label = "ready"), viewModel.states.value)
        assertEquals(1, viewModel.readCounter())
    }

    @Test
    fun concurrentUpdatesDoNotLoseWrites() = runTest(dispatcher) {
        val viewModel = TestViewModel()

        viewModel.incrementInBackground(times = 100)
        advanceUntilIdle()

        assertEquals(100, viewModel.states.value.counter)
    }

    @Test
    fun actionEmittedBeforeAnyCollectorIsBuffered() = runTest(dispatcher) {
        val viewModel = TestViewModel()

        viewModel.emit(TestAction.Closed)
        advanceUntilIdle()

        // The collector attaches only now — a Channel-backed flow must still deliver the action.
        assertEquals(TestAction.Closed, viewModel.actions.first())
    }

    @Test
    fun actionsArriveInEmissionOrder() = runTest(dispatcher) {
        val viewModel = TestViewModel()
        val collected = async { viewModel.actions.take(3).toList() }

        viewModel.emit(TestAction.Opened(1))
        viewModel.emit(TestAction.Opened(2))
        viewModel.emit(TestAction.Closed)
        advanceUntilIdle()

        assertEquals(
            listOf(TestAction.Opened(1), TestAction.Opened(2), TestAction.Closed),
            collected.await()
        )
    }
}
