package com.example.organize3.applications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.appUi.ApplicationUiState
import com.example.organize3.appUi.isValid
import com.example.organize3.appUi.toApplicationAccount
import com.example.organize3.appUi.toApplicationUiState
import com.example.organize3.data.application.ApplicationRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ApplicationEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val applicationRepository: ApplicationRepository
): ViewModel(){

    var applicationUiState by mutableStateOf(ApplicationUiState())
        private set

    private val applicationId: Int = checkNotNull(savedStateHandle["applicationId"])
    /**
     * Holds current item ui state
     */
    init {
        viewModelScope.launch {
            applicationUiState = applicationRepository.getApplicationStream(applicationId)
                .filterNotNull()
                .first()
                .toApplicationUiState(actionEnabled = true)
        }
    }
    fun updateUiState(newApplicationUiState: ApplicationUiState) {
        applicationUiState = newApplicationUiState.copy(actionEnabled = newApplicationUiState.isValid())
    }
    suspend fun updateApplication() {
        if (applicationUiState.isValid()) {
            applicationRepository.updateApplication(applicationUiState.toApplicationAccount())
        }
    }
}