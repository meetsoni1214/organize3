package com.example.organize3.emailAccounts.details

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.email.EmailAccount
import com.example.organize3.data.email.repository.MongoDB
import com.example.organize3.emailAccounts.add_update.EmailUiState
import com.example.organize3.util.Constants
import com.example.organize3.util.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.BsonObjectId

class EmailDetailViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel(){

    var uiState by mutableStateOf(EmailUiState())
        private set

    init {
        getEmailIdArgument()
        fetchSelectedEmailAccount()
    }
    private fun getEmailIdArgument() {
        uiState = uiState.copy(
            selectedEmailId = savedStateHandle.get<String>(key = Constants.EMAIL_SCREEN_ARGUMENT_KEY)
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

    fun deleteEmailAccount(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.selectedEmailId != null) {
                val result = MongoDB.deleteDiary(id = org.mongodb.kbson.ObjectId.invoke(uiState.selectedEmailId!!))
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

    fun duplicateEmailAccount(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO){
            val result = MongoDB.insertEmailAccount(emailAccount = EmailAccount().apply {
                title = "copy of ${uiState.title}"
                email = uiState.email
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

    fun archiveEmailAccount(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO){
            val result = MongoDB.updateEmailAccount(emailAccount = EmailAccount().apply {
                _id = org.mongodb.kbson.ObjectId.invoke(uiState.selectedEmailId!!)
                title = uiState.title
                email = uiState.email
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

    private fun setTitle(title: String) {
        uiState = uiState.copy(
            title = title
        )
    }
    private fun setEmail(email: String) {
        uiState = uiState.copy(
            email = email
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