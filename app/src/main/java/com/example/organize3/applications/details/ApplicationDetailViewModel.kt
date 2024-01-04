package com.example.organize3.applications.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.application.ApplicationAccount
import com.example.organize3.data.email.repository.MongoDB
import com.example.organize3.util.Constants
import com.example.organize3.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApplicationDetailViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    var uiState by mutableStateOf(ApplicationUiState())
        private set

    init {
        getApplicationIdArgument()
        fetchSelectedApplicationAccount()
    }
    private fun getApplicationIdArgument() {
        uiState = uiState.copy(
            selectedApplicationId  = savedStateHandle.get<String>(key = Constants.APPLICATION_SCREEN_ARGUMENT_KEY)
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

    fun deleteApplicationAccount(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedApplicationId != null) {
                val result = MongoDB.deleteApplicationAccount(id = org.mongodb.kbson.ObjectId.invoke(uiState.selectedApplicationId!!))
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

    fun duplicateApplicationAccount(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedApplicationId != null) {
                val result = MongoDB.insertApplicationAccount(applicationAccount = ApplicationAccount().apply {
                    title = "copy of ${uiState.title}"
                    username = uiState.username
                    password = uiState.password
                    remarks = uiState.remarks
                    isArchived = uiState.isArchived
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

    fun archiveApplicationAccount(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedApplicationId != null) {
                val result = MongoDB.updateApplicationAccount(applicationAccount = ApplicationAccount().apply {
                   _id = org.mongodb.kbson.ObjectId.invoke(uiState.selectedApplicationId!!)
                    title = uiState.title
                    username = uiState.username
                    password = uiState.password
                    remarks = uiState.remarks
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
    private fun setTitle(title: String) {
        uiState = uiState.copy(
            title = title
        )
    }
    private fun setUsername(username: String) {
        uiState = uiState.copy(
            username = username
        )
    }
    private fun setPassword(password: String) {
        uiState = uiState.copy(
            password = password
        )
    }

    private fun setRemarks(remarks: String) {
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

data class ApplicationUiState(
    val selectedApplicationId: String? = null,
    val title: String = "",
    val username: String = "",
    val password: String = "",
    val remarks: String = "",
    val isArchived: Int = 0
)