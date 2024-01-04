package com.example.organize3.applications.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.application.ApplicationAccount
import com.example.organize3.data.email.repository.ApplicationAccounts
import com.example.organize3.data.email.repository.MongoDB
import com.example.organize3.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * View Model to retrieve all items in the Room database.
 */
class ApplicationViewModel: ViewModel(){
    var applicationAccounts: MutableState<ApplicationAccounts> = mutableStateOf(RequestState.Idle)

    init {
        observeAllApplicationAccounts()
    }

    private fun observeAllApplicationAccounts() {
        viewModelScope.launch {
            MongoDB.getAllApplicationAccounts().collect { result ->
                applicationAccounts.value = result
            }
        }
    }

    fun archiveApplication(
        applicationAccount: ApplicationAccount,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = MongoDB.updateApplicationAccount(applicationAccount = applicationAccount.apply {
                isArchived = 1
            })
            if (result is RequestState.Success) {
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            }else if (result is RequestState.Error) {
                withContext(Dispatchers.Main) {
                    onError(result.error.message.toString())
                }
            }
        }
    }
}