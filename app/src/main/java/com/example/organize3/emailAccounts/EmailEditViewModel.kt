package com.example.organize3.emailAccounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.appUi.EmailUiState
import com.example.organize3.appUi.isValid
import com.example.organize3.appUi.toEmailAccount
import com.example.organize3.appUi.toEmailUiState
import com.example.organize3.data.email.EmailRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EmailEditViewModel (
    savedStateHandle: SavedStateHandle,
    private val emailRepository: EmailRepository
        ): ViewModel() {

    var emailUiState by mutableStateOf(EmailUiState())
        private set

    private val emailId: Int = checkNotNull(savedStateHandle["emailId"])
    /**
     * Holds current item ui state
     */
    init {
        viewModelScope.launch {
            emailUiState = emailRepository.getItemStream(emailId)
                .filterNotNull()
                .first()
                .toEmailUiState(actionEnabled = true)
        }
    }

    fun updateUiState(newEmailUiState: EmailUiState) {
        emailUiState = newEmailUiState.copy(actionEnabled = newEmailUiState.isValid())
    }

    suspend fun updateEmail() {
        if (emailUiState.isValid()) {
            emailRepository.updateEmail(emailUiState.toEmailAccount())
        }
     }
}