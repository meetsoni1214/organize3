package com.example.organize3.bankAccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.bankAccount.BankAccount
import com.example.organize3.data.bankAccount.BankAccountRepository
import kotlinx.coroutines.flow.*


/**
 * View Model to retrieve all items in the Room database.
 */
class BankAccountHomeViewModel(
    private val bankAccountRepository: BankAccountRepository
): ViewModel() {
    val bankAccountHomeUiState: StateFlow<BankAccountHomeUiState> = bankAccountRepository.getAllBankAccountsStream().map { BankAccountHomeUiState(it) }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = BankAccountHomeUiState()
    )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    val bankAccounts = searchText
        .combine(bankAccountHomeUiState) {text, uiState ->
            if (text.isBlank()) {
                uiState.bankAccounts
            } else {
                uiState.bankAccounts.filter {
                    it.doesMatchSearchQuery(text)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = bankAccountHomeUiState.value.bankAccounts
        )
    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    suspend fun archiveBankAccount(bankAccount: BankAccount) {
        bankAccountRepository.updateBankAccount(bankAccount.copy(isArchived = 1))
    }
    suspend fun undoArchiveBankAccount(bankAccount: BankAccount) {
        bankAccountRepository.updateBankAccount(bankAccount.copy(isArchived = 0))
    }
}

/**
 * Ui State for EmailHomeScreen
 */
data class BankAccountHomeUiState(val bankAccounts: List<BankAccount> = listOf())

