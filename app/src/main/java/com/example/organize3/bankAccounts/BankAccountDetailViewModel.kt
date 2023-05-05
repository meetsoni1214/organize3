package com.example.organize3.bankAccounts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.appUi.BankAccountUiState
import com.example.organize3.appUi.toBankAccount
import com.example.organize3.appUi.toBankAccountUiState
import com.example.organize3.data.bankAccount.BankAccount
import com.example.organize3.data.bankAccount.BankAccountRepository
import kotlinx.coroutines.flow.*

class BankAccountDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val bankAccountRepository: BankAccountRepository
): ViewModel() {
    private val bankAccountId: Int = checkNotNull(savedStateHandle["bankAccountId"])

    val uiState: StateFlow<BankAccountUiState> = bankAccountRepository.getBankAccountStream(bankAccountId)
        .filterNotNull()
        .map {
            it.toBankAccountUiState()
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = BankAccountUiState()
        )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun archiveBankAccount() {
        bankAccountRepository.updateBankAccount(uiState.value.copy(isArchived = 1).toBankAccount())
    }
    suspend fun duplicateBankAccount() {
        bankAccountRepository.insertBankAccount(
            BankAccount(bankName = "copy of ${uiState.value.bankName}", bankLogo = uiState.value.bankLogo, accountHolderName = uiState.value.accountHolderName,
            accountNumber = uiState.value.accountNumber,
            accountType = uiState.value.accountType,
            remarks = uiState.value.remarks,
            ifscCode = uiState.value.ifscCode,
            regMobNo = uiState.value.regMobNo,
            regEmail = uiState.value.regEmail,
            haveCard = uiState.value.haveCard,
            nameOnCard = uiState.value.nameOnCard,
            expiryDate = uiState.value.expiryDate,
            cardCvv = uiState.value.cardCvv,
            cardNo = uiState.value.cardNo,
            cardPin = uiState.value.cardPin,
            haveUpi = uiState.value.haveUpi,
            upiPin = uiState.value.upiPin,
            haveBankingApp = uiState.value.haveBankingApp,
            loginPin = uiState.value.loginPin,
            transactionPin = uiState.value.transactionPin
            )
        )
    }
}