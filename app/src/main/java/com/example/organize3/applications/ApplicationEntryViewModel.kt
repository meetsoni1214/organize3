package com.example.organize3.applications

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.organize3.appUi.ApplicationUiState
import com.example.organize3.appUi.isValid
import com.example.organize3.appUi.toApplicationAccount
import com.example.organize3.data.application.ApplicationRepository


/**
 * View Model to validate and insert items in the Room database.
 */
class ApplicationEntryViewModel(
    private val applicationRepository: ApplicationRepository
):ViewModel(){

    /**
     * Holds current item ui state
     */
    var applicationUiState by mutableStateOf(ApplicationUiState())
        private set


    /**
     * Updates the [applicationUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */

    fun updateUiState(newApplicationUiState: ApplicationUiState) {
        applicationUiState = newApplicationUiState.copy(actionEnabled = newApplicationUiState.isValid())
    }
    suspend fun saveApplication() {
        if (applicationUiState.isValid()) {
            applicationRepository.insertApplication(applicationUiState.toApplicationAccount())
        }
    }
}