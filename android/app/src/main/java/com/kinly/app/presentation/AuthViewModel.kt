package com.kinly.app.presentation

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kinly.app.auth.AuthRepository
import com.kinly.app.auth.AuthResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Checking : AuthUiState
    data object SignedOut : AuthUiState
    data object OpeningBrowser : AuthUiState
    data class SignedIn(val displayName: String?) : AuthUiState
    data object Cancelled : AuthUiState
    data object Expired : AuthUiState
    data object Offline : AuthUiState
    data object NotConfigured : AuthUiState
    data object Unavailable : AuthUiState
    data object Failed : AuthUiState
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    private val mutableState = MutableStateFlow<AuthUiState>(AuthUiState.Checking)
    val state: StateFlow<AuthUiState> = mutableState.asStateFlow()
    private var work: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        if (!repository.configured) {
            mutableState.value = AuthUiState.NotConfigured
            return
        }
        work?.cancel()
        work = viewModelScope.launch {
            mutableState.value = AuthUiState.Checking
            mutableState.value = try {
                repository.restore().toUiState()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                AuthUiState.Unavailable
            }
        }
    }

    fun beginSignIn(): Intent? {
        if (!repository.configured) {
            mutableState.value = AuthUiState.NotConfigured
            return null
        }
        return try {
            repository.authorizationIntent()?.also {
                mutableState.value = AuthUiState.OpeningBrowser
            } ?: run {
                mutableState.value = AuthUiState.Unavailable
                null
            }
        } catch (_: Exception) {
            mutableState.value = AuthUiState.Failed
            null
        }
    }

    fun complete(result: Intent?) {
        work?.cancel()
        work = viewModelScope.launch {
            mutableState.value = AuthUiState.Checking
            mutableState.value = try {
                repository.complete(result).toUiState()
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Exception) {
                AuthUiState.Failed
            }
        }
    }

    fun signOut() {
        work?.cancel()
        repository.clear()
        mutableState.value = if (repository.configured) AuthUiState.SignedOut else AuthUiState.NotConfigured
    }

    override fun onCleared() {
        (repository as? AutoCloseable)?.close()
        super.onCleared()
    }

    private fun AuthResult.toUiState(): AuthUiState = when (this) {
        AuthResult.SignedOut -> AuthUiState.SignedOut
        is AuthResult.SignedIn -> AuthUiState.SignedIn(profile.displayName)
        AuthResult.Cancelled -> AuthUiState.Cancelled
        AuthResult.Expired -> AuthUiState.Expired
        AuthResult.Offline -> AuthUiState.Offline
        AuthResult.Unavailable -> AuthUiState.Unavailable
        AuthResult.Failed -> AuthUiState.Failed
    }

    companion object {
        fun factory(repository: AuthRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(AuthViewModel::class.java))
                    return AuthViewModel(repository) as T
                }
            }
    }
}
