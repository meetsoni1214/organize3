package com.example.organize3.emailAccounts.add_update

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.email.EmailAccount
import com.example.organize3.data.email.repository.MongoDB
import com.example.organize3.util.Constants.EMAIL_SCREEN_ARGUMENT_KEY
import com.example.organize3.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WriteEmailViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    var uiState by mutableStateOf(EmailUiState())
        private set

    init {
        getEmailIdArgument()
        fetchSelectedEmailAccount()
    }

    private fun getEmailIdArgument() {
        uiState = uiState.copy(
            selectedEmailId = savedStateHandle.get<String>(key = EMAIL_SCREEN_ARGUMENT_KEY)
        )
    }

    private fun fetchSelectedEmailAccount() {
        if (uiState.selectedEmailId != null) {
            viewModelScope.launch(Dispatchers.Main) {
                MongoDB.getSelectedEmailAccount(
                    emailId = org.mongodb.kbson.ObjectId.invoke(uiState.selectedEmailId!!)
                )
                    .catch {
                        emit(RequestState.Error(Exception("Diary is already deleted! ")))
                    }
                    .collect { emailAccount ->
                        if (emailAccount is RequestState.Success) {
                            setTitle(emailAccount.data.title)
                            setEmail(emailAccount.data.email)
                            setPassword(emailAccount.data.password)
                            setRemarks(emailAccount.data.remarks)
                            setIsArchived(emailAccount.data.isArchived)
                        }
                    }
            }
        }
    }
    private suspend fun insertEmailAccount(
        emailAccount: EmailAccount,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.insertEmailAccount(emailAccount = emailAccount)
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

    fun upsertEmailAccount(
        emailAccount: EmailAccount,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedEmailId != null) {
                updateEmailAccount(emailAccount = emailAccount, onSuccess = onSuccess, onError = onError)
            } else {
                insertEmailAccount(emailAccount = emailAccount, onSuccess = onSuccess, onError = onError)
            }
        }
    }

    private suspend fun updateEmailAccount(
        emailAccount: EmailAccount,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val result = MongoDB.updateEmailAccount(emailAccount = emailAccount.apply {
            _id = org.mongodb.kbson.ObjectId.invoke(uiState.selectedEmailId!!)
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

    fun setTitle(title: String) {
        uiState = uiState.copy(
            title = title
        )
    }
    fun setEmail(email: String) {
        uiState = uiState.copy(
            email = email
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

    fun setIsArchived(isArchived: Int) {
        uiState = uiState.copy(
            isArchived = isArchived
        )
    }
}

data class EmailUiState (
    val selectedEmailId: String? = null,
    val title: String = "",
    val email: String = "",
    val password: String = "",
    val remarks: String = "",
    val isArchived: Int = 0,
)