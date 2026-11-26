package com.kinly.app.presentation

import android.content.Intent
import com.kinly.app.auth.AuthRepository
import com.kinly.app.auth.AuthResult
import com.kinly.app.auth.MinimalProfile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    @get:Rule val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun missingProviderShowsUnavailableSetup() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeRepository(configured = false)
        val model = AuthViewModel(repository)
        advanceUntilIdle()
        assertEquals(AuthUiState.NotConfigured, model.state.value)
        assertEquals(null, model.beginSignIn())
        assertEquals(AuthUiState.NotConfigured, model.state.value)
    }

    @Test
    fun cancellationOfflineExpiryAndLogoutStayHonest() = runTest(mainDispatcherRule.dispatcher) {
        val repository = FakeRepository(configured = true)
        val model = AuthViewModel(repository)
        advanceUntilIdle()
        assertEquals(AuthUiState.SignedOut, model.state.value)

        repository.completion = AuthResult.Cancelled
        model.complete(null)
        advanceUntilIdle()
        assertEquals(AuthUiState.Cancelled, model.state.value)

        repository.restored = AuthResult.Offline
        model.refresh()
        advanceUntilIdle()
        assertEquals(AuthUiState.Offline, model.state.value)

        repository.restored = AuthResult.Expired
        model.refresh()
        advanceUntilIdle()
        assertEquals(AuthUiState.Expired, model.state.value)

        repository.completion = AuthResult.SignedIn(
            MinimalProfile("opaque-id", "adult-one@example.invalid", "Adult One"),
        )
        model.complete(null)
        advanceUntilIdle()
        assertEquals(AuthUiState.SignedIn("Adult One"), model.state.value)
        model.signOut()
        assertEquals(AuthUiState.SignedOut, model.state.value)
        assertTrue(repository.cleared)
    }

    private class FakeRepository(
        override val configured: Boolean,
    ) : AuthRepository {
        var restored: AuthResult = AuthResult.SignedOut
        var completion: AuthResult = AuthResult.SignedOut
        var cleared = false
        override fun authorizationIntent(): Intent? = null
        override suspend fun restore(): AuthResult = restored
        override suspend fun complete(result: Intent?): AuthResult = completion
        override fun clear() {
            cleared = true
        }
    }
}
