package com.example.organize3.bankAccounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.organize3.appUi.BankAccountUiState
import com.example.organize3.appUi.isValid
import com.example.organize3.appUi.toBankAccount
import com.example.organize3.data.bankAccount.BankAccountRepository

/**
 * View Model to validate and insert items in the Room database.
 */
class BankAccountEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val bankAccountRepository: BankAccountRepository
):ViewModel(){

    /**
     * Holds current item ui state
     */

    private val bName: String = checkNotNull(savedStateHandle["bName"])
    private val bLogo: Int = checkNotNull(savedStateHandle["bLogo"])

    var bankAccountUiState by mutableStateOf(BankAccountUiState(bankName = bName, bankLogo = bLogo))
    private set


    /**
     * Updates the [bankAccountUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */

    fun updateUiState(newBankAccountUiState: BankAccountUiState) {
        bankAccountUiState = newBankAccountUiState.copy(actionEnabled = newBankAccountUiState.isValid())
    }

    suspend fun saveBankAccount() {
        if (bankAccountUiState.isValid()) {
            bankAccountRepository.insertBankAccount(bankAccountUiState.toBankAccount())
        }
    }

}