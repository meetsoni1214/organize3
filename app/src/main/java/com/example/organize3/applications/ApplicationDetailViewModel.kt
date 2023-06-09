package com.example.organize3.applications

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.appUi.ApplicationUiState
import com.example.organize3.appUi.toApplicationAccount
import com.example.organize3.appUi.toApplicationUiState
import com.example.organize3.data.application.ApplicationAccount
import com.example.organize3.data.application.ApplicationRepository
import kotlinx.coroutines.flow.*

class ApplicationDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val applicationRepository: ApplicationRepository
): ViewModel() {

    private val applicationId: Int = checkNotNull(savedStateHandle["applicationId"])
    val isArchived: Int = checkNotNull(savedStateHandle["isArchived"])

    val uiState: StateFlow<ApplicationUiState> = applicationRepository.getApplicationStream(applicationId)
        .filterNotNull()
        .map {
            it.toApplicationUiState()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ApplicationUiState()
        )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L

    }

    suspend fun archiveApplication() {
        applicationRepository.updateApplication(uiState.value.copy(isArchived = 1).toApplicationAccount())
    }
    suspend fun deleteApplication(applicationAccount: ApplicationAccount) {
        applicationRepository.deleteApplication(applicationAccount)
    }
    suspend fun unArchiveApplication() {
        applicationRepository.updateApplication(uiState.value.copy(isArchived = 0).toApplicationAccount())
    }
    suspend fun duplicateApplication() {
//        val title = uiState.value.title
//        val newCopy = uiState.value.copy(title = "copy of $title")
        applicationRepository.insertApplication(ApplicationAccount(accountTitle = "copy of ${uiState.value.title}", accountUsername = uiState.value.username, accountPassword = uiState.value.password, accountRemarks = uiState.value.remarks, isArchived = uiState.value.isArchived))
    }
}