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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EmailViewModel: ViewModel() {

    var emailAccounts: MutableState<EmailAccounts> = mutableStateOf(RequestState.Idle)
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()


//    private val _emails = MutableStateFlow(
//        if (emailAccounts.value is RequestState.Success) (emailAccounts.value as RequestState.Success).data else listOf<EmailAccount>()
//    )
//    val emails = searchText
//        .onEach { _isSearching.update { true } }
//        .combine(_emails) {text, emails ->
//            if (text.isBlank()) {
//                emails
//            } else {
//                 emails.filter {
//                    it.doesMatchSearchQuery(text)
//                }
//            }
//        }
//            .onEach { _isSearching.update { false } }
//            .stateIn(
//            viewModelScope,
//            SharingStarted.WhileSubscribed(5000),
//            _emails.value
//        )

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

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