package com.example.organize3.applications.add_update

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.applications.details.ApplicationUiState
import com.example.organize3.data.application.ApplicationAccount
import com.example.organize3.data.email.repository.MongoDB
import com.example.organize3.util.Constants.APPLICATION_SCREEN_ARGUMENT_KEY
import com.example.organize3.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApplicationEditViewModel(
    private val savedStateHandle: SavedStateHandle,
): ViewModel(){
    var uiState by mutableStateOf(ApplicationUiState())
        private set

    init {
        getApplicationIdArgument()
        fetchSelectedApplicationAccount()
    }

    private fun getApplicationIdArgument() {
        uiState = uiState.copy(
            selectedApplicationId = savedStateHandle.get<String>(key = APPLICATION_SCREEN_ARGUMENT_KEY)
        )
    }
    private fun fetchSelectedApplicationAccount() {
        if (uiState.selectedApplicationId != null) {
            viewModelScope.launch(Dispatchers.Main) {
                MongoDB.getSelectedApplicationAccount(
                    appId = org.mongodb.kbson.ObjectId.invoke(uiState.selectedApplicationId!!)
                )
                    .catch {
                        emit(RequestState.Error(Exception("Application Account is already deleted! ")))
                    }
                    .collect { applicationAccount ->
                        if (applicationAccount is RequestState.Success) {
                            setTitle(applicationAccount.data.title)
                            setUsername(applicationAccount.data.username)
                            setPassword(applicationAccount.data.password)
                            setRemarks(applicationAccount.data.remarks)
                            setIsArchived(applicationAccount.data.isArchived)
                        }
                    }
            }
        }
    }

    private suspend fun insertApplicationAccount(
        applicationAccount: ApplicationAccount,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.insertApplicationAccount(applicationAccount = applicationAccount)
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
    private suspend fun updateApplicationAccount(
        applicationAccount: ApplicationAccount,
        onError: (String) -> Unit,
        onSuccess: () -> Unit
    ) {
        val result = MongoDB.updateApplicationAccount(applicationAccount = applicationAccount.apply {
            _id = org.mongodb.kbson.ObjectId.invoke(uiState.selectedApplicationId!!)
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

    fun upsertApplicationAccount(
        applicationAccount: ApplicationAccount,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedApplicationId != null) {
                updateApplicationAccount(applicationAccount = applicationAccount, onSuccess = onSuccess, onError = onError)
            } else {
                insertApplicationAccount(applicationAccount = applicationAccount, onSuccess = onSuccess, onError = onError)
            }
        }
    }
    fun setTitle(title: String) {
        uiState = uiState.copy(
            title = title
        )
    }
    fun setUsername(username: String) {
        uiState = uiState.copy(
            username = username
        )
    }
    fun setPassword(password: String) {
        uiState = uiState.copy(
            password = password
        )
    }

    fun setRemarks(remarks: String) {
        uiState = uiState.copy(
            remarks = remarks
        )
    }

    private fun setIsArchived(isArchived: Int) {
        uiState = uiState.copy(
            isArchived = isArchived
        )
    }
}