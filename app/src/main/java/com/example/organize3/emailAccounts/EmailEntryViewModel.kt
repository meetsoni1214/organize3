package com.example.organize3.emailAccounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.organize3.appUi.EmailUiState
import com.example.organize3.appUi.isValid
import com.example.organize3.appUi.toEmailAccount
import com.example.organize3.data.email.EmailRepository

/**
 * View Model to validate and insert items in the Room database.
 */
class EmailEntryViewModel(private val emailRepository: EmailRepository): ViewModel() {

    /**
     * Holds current item ui state
     */
    var emailUiState by mutableStateOf(EmailUiState())
        private set

    /**
     * Updates the [emailUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(newEmailUiState: EmailUiState) {
        emailUiState = newEmailUiState.copy(actionEnabled = newEmailUiState.isValid())
    }
    suspend fun saveEmail() {
        if (emailUiState.isValid()) {
            emailRepository.insertEmail(emailUiState.toEmailAccount())
        }
    }
}