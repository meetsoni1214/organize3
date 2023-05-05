package com.example.organize3.archived

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.application.ApplicationAccount
import com.example.organize3.data.application.ApplicationRepository
import com.example.organize3.data.bankAccount.BankAccount
import com.example.organize3.data.bankAccount.BankAccountRepository
import com.example.organize3.data.email.EmailAccount
import com.example.organize3.data.email.EmailRepository
import com.example.organize3.data.folderWithNotes.FolderWithNotesRepository
import com.example.organize3.data.folderWithNotes.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ArchivedHomeViewModel(
    private val emailRepository: EmailRepository,
    private val applicationRepository: ApplicationRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val notesRepository: FolderWithNotesRepository
): ViewModel(){

    val archivedHomeUiState: StateFlow<ArchivedHomeUiState> = emailRepository.getArchivedEmailsStream().map { ArchivedHomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = ArchivedHomeUiState()
        )


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
data class ArchivedHomeUiState(
    val emailList: List<EmailAccount> = listOf(),
    val applicationList: List<ApplicationAccount> = listOf(),
    val bankAccountList: List<BankAccount> = listOf(),
    val notesList: List<Note> = listOf()
)