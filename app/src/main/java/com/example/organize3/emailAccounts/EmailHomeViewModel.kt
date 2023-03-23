package com.example.organize3.emailAccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.email.EmailAccount
import com.example.organize3.data.email.EmailRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * View Model to retrieve all items in the Room database.
 */
class EmailHomeViewModel(
    private val emailRepository: EmailRepository
): ViewModel(){
    val emailHomeUiState: StateFlow<EmailHomeUiState> = emailRepository.getAllEmailsStream().map { EmailHomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = EmailHomeUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    suspend fun deleteEmail(emailAccount: EmailAccount) {
        emailRepository.deleteEmail(emailAccount)
    }
}
/**
 * Ui State for EmailHomeScreen
 */
data class EmailHomeUiState(val emailList: List<EmailAccount> = listOf())