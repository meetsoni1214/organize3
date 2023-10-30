package com.example.organize3

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.organize3.applications.ApplicationDetailViewModel
import com.example.organize3.applications.ApplicationEditViewModel
import com.example.organize3.applications.ApplicationEntryViewModel
import com.example.organize3.applications.ApplicationViewModel
import com.example.organize3.archived.ArchivedHomeViewModel
import com.example.organize3.bankAccounts.BankAccountDetailViewModel
import com.example.organize3.bankAccounts.BankAccountEditViewModel
import com.example.organize3.bankAccounts.BankAccountEntryViewModel
import com.example.organize3.bankAccounts.BankAccountHomeViewModel

import com.example.organize3.folder.FolderHomeViewModel
import com.example.organize3.notes.NoteEntryViewModel
import com.example.organize3.notes.NotesHomeViewModel


/**
 * Provides Factory to create instance of ViewModel for the entire Organize app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {

        // Initializer for ApplicationViewModel
        initializer {
            ApplicationViewModel(
                applicationRepository = organizeApplication().container.applicationRepository,
                userPreferencesRepository = organizeApplication().userPreferencesRepository
            )
        }
        // Initializer for ApplicationEntryViewModel
        initializer {
            ApplicationEntryViewModel(
                applicationRepository = organizeApplication().container.applicationRepository
            )
        }
        // Initializer for ApplicationDetailViewModel
        initializer {
            ApplicationDetailViewModel(
                applicationRepository = organizeApplication().container.applicationRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
        // Initializer for ApplicationEditViewModel
        initializer {
            ApplicationEditViewModel(
                applicationRepository = organizeApplication().container.applicationRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
        // Initializer for BankAccountEntryViewModel
        initializer {
            BankAccountEntryViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                bankAccountRepository = organizeApplication().container.bankAccountRepository
            )
        }
        // Initializer for BankAccountHomeViewModel
        initializer {
            BankAccountHomeViewModel(
                bankAccountRepository = organizeApplication().container.bankAccountRepository
            )
        }
        // Initializer for BankAccountDetailViewModel
        initializer {
            BankAccountDetailViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                bankAccountRepository = organizeApplication().container.bankAccountRepository
            )
        }
        // Initializer for BankAccountEditViewModel
        initializer {
            BankAccountEditViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                bankAccountRepository = organizeApplication().container.bankAccountRepository
            )
        }
        // Initializer for FolderHomeViewModel
        initializer {
            FolderHomeViewModel(
                folderRepository = organizeApplication().container.folderWithNotesRepository
            )
        }
        // Initializer for NotesHomeViewModel
        initializer {
            NotesHomeViewModel(
                noteRepository = organizeApplication().container.folderWithNotesRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
        // Initializer for AddNoteViewModel
        initializer {
            NoteEntryViewModel(
                noteRepository = organizeApplication().container.folderWithNotesRepository,
                savedStateHandle = this.createSavedStateHandle()
            )
        }
        // Initializer for ArchivedHomeViewModel
        initializer {
            ArchivedHomeViewModel(
//                emailRepository = organizeApplication().container.emailRepository,
                notesRepository = organizeApplication().container.folderWithNotesRepository,
                applicationRepository = organizeApplication().container.applicationRepository,
                bankAccountRepository =  organizeApplication().container.bankAccountRepository
            )
        }
      }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [OrganizeApplication].
 */
fun CreationExtras.organizeApplication(): OrganizeApp =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as OrganizeApp)