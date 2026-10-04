package com.wojciechkula.deepskyapp.core.mvvm

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

private enum class ScreenPhase { LOADING, CONTENT, ERROR }

private data class TrackedState(val phase: ScreenPhase = ScreenPhase.LOADING, val counter: Int = 0)

private class RecordingHandler : AnalyticsStateHandler<TrackedState>() {
    val sent = mutableListOf<String>()

    override fun mapStateToScreenName(state: TrackedState): String? = when (state.phase) {
        ScreenPhase.LOADING -> null
        ScreenPhase.CONTENT -> "content"
        ScreenPhase.ERROR -> "error"
    }

    override fun sendScreenView(screenName: String) {
        sent += screenName
    }
}

private class TrackedViewModel(
    handler: AnalyticsStateHandler<TrackedState>,
    initialState: TrackedState = TrackedState()
) : StateActionsViewModel<TrackedState, Nothing>(initialState, handler) {
    fun show(phase: ScreenPhase) = updateState { copy(phase = phase) }
    fun increment() = updateState { copy(counter = counter + 1) }
}

@OptIn(ExperimentalCoroutinesApi::class)
class AnalyticsStateHandlerTest {

    private val dispatcher = StandardTestDispatcher()
    private val handler = RecordingHandler()

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun stateMappedToNullLogsNothing() = runTest(dispatcher) {
        TrackedViewModel(handler)
        advanceUntilIdle()

        assertEquals(emptyList(), handler.sent)
    }

    @Test
    fun initialStateWithANameIsLoggedOnInit() = runTest(dispatcher) {
        TrackedViewModel(handler, TrackedState(phase = ScreenPhase.CONTENT))
        advanceUntilIdle()

        assertEquals(listOf("content"), handler.sent)
    }

    @Test
    fun aChangedNameIsLoggedAndAStateKeepingTheNameIsNot() = runTest(dispatcher) {
        val viewModel = TrackedViewModel(handler)
        advanceUntilIdle()

        viewModel.show(ScreenPhase.CONTENT)
        advanceUntilIdle()
        viewModel.increment()
        advanceUntilIdle()
        viewModel.show(ScreenPhase.ERROR)
        advanceUntilIdle()

        assertEquals(listOf("content", "error"), handler.sent)
    }

    @Test
    fun firstDisplayAfterAnInitLogIsSkipped() = runTest(dispatcher) {
        val viewModel = TrackedViewModel(handler, TrackedState(phase = ScreenPhase.CONTENT))
        advanceUntilIdle()

        viewModel.onScreenDisplayed()

        assertEquals(listOf("content"), handler.sent)
    }

    @Test
    fun returningToTheScreenLogsTheCurrentNameAgain() = runTest(dispatcher) {
        val viewModel = TrackedViewModel(handler, TrackedState(phase = ScreenPhase.CONTENT))
        advanceUntilIdle()

        viewModel.onScreenDisplayed()
        viewModel.onScreenDisplayed()

        assertEquals(listOf("content", "content"), handler.sent)
    }

    @Test
    fun firstDisplayWhileLoadingDoesNotSwallowTheLaterContentLog() = runTest(dispatcher) {
        val viewModel = TrackedViewModel(handler)
        advanceUntilIdle()

        viewModel.onScreenDisplayed()
        viewModel.show(ScreenPhase.CONTENT)
        advanceUntilIdle()

        assertEquals(listOf("content"), handler.sent)
    }

    @Test
    fun aChangeWhileHiddenIsLoggedOnceOnReturn() = runTest(dispatcher) {
        val viewModel = TrackedViewModel(handler, TrackedState(phase = ScreenPhase.CONTENT))
        advanceUntilIdle()
        viewModel.onScreenDisplayed()

        viewModel.onScreenHidden()
        viewModel.show(ScreenPhase.ERROR)
        advanceUntilIdle()
        assertEquals(listOf("content"), handler.sent)

        viewModel.onScreenDisplayed()
        assertEquals(listOf("content", "error"), handler.sent)
    }
}
