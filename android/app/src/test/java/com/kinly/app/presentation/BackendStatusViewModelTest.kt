package com.kinly.app.presentation

import com.kinly.app.data.BackendStatusRepository
import java.io.IOException
import java.util.ArrayDeque
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BackendStatusViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun reportsAvailableWhenRepositoryIsReady() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = BackendStatusViewModel(BackendStatusRepository { true })

        advanceUntilIdle()

        assertEquals(BackendStatusUiState.Available, viewModel.state.value)
    }

    @Test
    fun reportsUnavailableWhenRequestFails() = runTest(mainDispatcherRule.dispatcher) {
        val viewModel = BackendStatusViewModel(
            BackendStatusRepository { throw IOException("offline") },
        )

        advanceUntilIdle()

        assertEquals(BackendStatusUiState.Unavailable, viewModel.state.value)
    }

    @Test
    fun retryChecksAgain() = runTest(mainDispatcherRule.dispatcher) {
        val responses = ArrayDeque(listOf(false, true))
        val viewModel = BackendStatusViewModel(
            BackendStatusRepository { responses.removeFirst() },
        )
        advanceUntilIdle()
        assertEquals(BackendStatusUiState.Unavailable, viewModel.state.value)

        viewModel.refresh()
        advanceUntilIdle()

        assertEquals(BackendStatusUiState.Available, viewModel.state.value)
    }
}
