package com.example.organize3.emailAccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.email.EmailAccount
import com.example.organize3.data.email.EmailRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * View Model to retrieve all items in the Room database.
 */
@OptIn(FlowPreview::class)
class EmailHomeViewModel(
    private val emailRepository: EmailRepository
): ViewModel(){



    val emailHomeUiState: StateFlow<EmailHomeUiState> = emailRepository.getAllEmailsStream().map { EmailHomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = EmailHomeUiState()
        )

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()


    val emailAccounts = searchText
        .combine(emailHomeUiState) {text, uiState ->
            if (text.isBlank()) {
                uiState.emailList
            }else {
                uiState.emailList.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = emailHomeUiState.value.emailList
        )



    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    fun archiveEmail(emailAccount: EmailAccount) {
        viewModelScope.launch {
            emailRepository.updateEmail(emailAccount.copy(isArchived = 1))
        }
    }
    suspend fun undoArchiveEmail(emailAccount: EmailAccount) {
        emailRepository.updateEmail(emailAccount.copy(isArchived = 0))
    }

}
/**
 * Ui State for EmailHomeScreen
 */
data class EmailHomeUiState(val emailList: List<EmailAccount> = listOf())