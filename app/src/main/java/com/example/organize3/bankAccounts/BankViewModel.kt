package com.example.organize3.bankAccounts

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.appUi.BankAccountUiState
import com.example.organize3.data.bankAccount.BankAccount
import com.example.organize3.data.DataSource.Banks
import com.example.organize3.model.Bank
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BankViewModel(application: Application): AndroidViewModel(application) {
    private val _bankAccount = MutableStateFlow(BankAccount())
    val bankAccount: StateFlow<BankAccount> = _bankAccount.asStateFlow()

    var bankAccountUiState by mutableStateOf(BankAccountUiState())
    private set
    var bankList = mutableStateOf(Banks)

    private var cachedBankList = listOf<Bank>()
    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    fun updateBankSearch(searchName: String) {
        _bankAccount.update { bankAccount ->
            bankAccount.copy(bankName = searchName)
        }
    }
    fun searchBankName(query: String) {
        val listToSearch = if(isSearchStarting) {
            bankList.value
        }else {
            cachedBankList
        }
        viewModelScope.launch {
            if (query.isEmpty()) {
                bankList.value = cachedBankList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                (it.bankName).contains(query.trim(), ignoreCase = true)
            }
            if (isSearchStarting) {
                cachedBankList = bankList.value
                isSearchStarting = false
            }
            bankList.value = results
            isSearching.value = true
        }
    }
}