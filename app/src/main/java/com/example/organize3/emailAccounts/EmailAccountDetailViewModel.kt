package com.example.organize3.emailAccounts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.appUi.EmailUiState
import com.example.organize3.appUi.toEmailAccount
import com.example.organize3.appUi.toEmailUiState
import com.example.organize3.data.email.EmailAccount
import com.example.organize3.data.email.EmailRepository
import kotlinx.coroutines.flow.*

class EmailAccountDetailViewModel (
    savedStateHandle: SavedStateHandle,
    private val emailRepository: EmailRepository
        ): ViewModel(){

    private val emailId: Int = checkNotNull(savedStateHandle["emailId"])

    val uiState:StateFlow<EmailUiState> = emailRepository.getItemStream(emailId)
        .filterNotNull()
        .map {
            it.toEmailUiState()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = EmailUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun deleteEmail() {
        emailRepository.deleteEmail(uiState.value.toEmailAccount())
    }

    suspend fun duplicateEmail() {
        emailRepository.insertEmail(EmailAccount(accountTitle = "copy of ${uiState.value.title}", accountEmail = uiState.value.email, accountPassword = uiState.value.password, accountRemarks = uiState.value.remarks))
    }

}