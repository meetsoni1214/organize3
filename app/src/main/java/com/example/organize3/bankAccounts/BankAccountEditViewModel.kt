package com.example.organize3.bankAccounts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.appUi.BankAccountUiState
import com.example.organize3.appUi.isValid
import com.example.organize3.appUi.toBankAccount
import com.example.organize3.appUi.toBankAccountUiState
import com.example.organize3.data.bankAccount.BankAccountRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BankAccountEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val bankAccountRepository: BankAccountRepository
): ViewModel() {

    var bankAccountUiState by mutableStateOf(BankAccountUiState())
    private set

    private val bankAccountId: Int = checkNotNull(savedStateHandle["bankAccountId"])
//    private val bName: String = checkNotNull(savedStateHandle["bName"])
//    private val bLogo: Int = checkNotNull(savedStateHandle["bLogo"])
    /**
     * Holds current item ui state
     */
    init {
        viewModelScope.launch {
            bankAccountUiState =  bankAccountRepository.getBankAccountStream(bankAccountId)
                .filterNotNull()
                .first()
                .toBankAccountUiState(
                    actionEnabled = true
                )
        }
    }
    fun updateUiState(newBankAccount: BankAccountUiState) {
        bankAccountUiState = newBankAccount.copy(actionEnabled = newBankAccount.isValid())
    }
    suspend fun updateBankAccount() {
        if (bankAccountUiState.isValid()) {
            bankAccountRepository.updateBankAccount(bankAccountUiState.toBankAccount())
        }
    }
}