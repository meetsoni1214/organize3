package com.example.organize3.bankAccounts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.bankAccount.BankAccount
import com.example.organize3.data.bankAccount.BankAccountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


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

