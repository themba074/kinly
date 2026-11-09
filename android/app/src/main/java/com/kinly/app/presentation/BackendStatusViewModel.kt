package com.kinly.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kinly.app.data.BackendStatusRepository
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BackendStatusUiState {
    data object Checking : BackendStatusUiState
    data object Available : BackendStatusUiState
    data object Unavailable : BackendStatusUiState
}

class BackendStatusViewModel(
    private val repository: BackendStatusRepository,
) : ViewModel() {
    private val mutableState = MutableStateFlow<BackendStatusUiState>(BackendStatusUiState.Checking)
    val state: StateFlow<BackendStatusUiState> = mutableState.asStateFlow()

    private var refreshJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        refreshJob?.cancel()
        refreshJob = viewModelScope.launch {
            mutableState.value = BackendStatusUiState.Checking
            mutableState.value = try {
                if (repository.isReady()) {
                    BackendStatusUiState.Available
                } else {
                    BackendStatusUiState.Unavailable
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: IOException) {
                BackendStatusUiState.Unavailable
            } catch (_: IllegalArgumentException) {
                BackendStatusUiState.Unavailable
            }
        }
    }

    companion object {
        fun factory(repository: BackendStatusRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    require(modelClass.isAssignableFrom(BackendStatusViewModel::class.java))
                    return BackendStatusViewModel(repository) as T
                }
            }
    }
}
