package com.example.organize3.emailAccounts.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.email.EmailAccount
import com.example.organize3.data.email.repository.EmailAccounts
import com.example.organize3.data.email.repository.MongoDB
import com.example.organize3.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmailViewModel: ViewModel() {

    var emailAccounts: MutableState<EmailAccounts> = mutableStateOf(RequestState.Idle)

    init {
        observeAllEmailAccounts()
    }
    private fun observeAllEmailAccounts() {
        viewModelScope.launch {
            MongoDB.getAllEmailAccounts().collect { result ->
                emailAccounts.value = result
            }
        }
    }

    fun archiveEmail(
        emailAccount: EmailAccount,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = MongoDB.updateEmailAccount(emailAccount = emailAccount.apply {
                isArchived = 1
            })
            if (result is RequestState.Success) {
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } else if (result is RequestState.Error) {
                withContext(Dispatchers.Main) {
                    onError(result.error.message.toString())
                }
            }
        }
    }
}