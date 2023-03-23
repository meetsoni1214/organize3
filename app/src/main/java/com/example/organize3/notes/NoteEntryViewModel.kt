package com.example.organize3.notes

import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.appUi.*
import com.example.organize3.data.folderWithNotes.FolderWithNotesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val noteRepository: FolderWithNotesRepository
): ViewModel() {

    val noteId: Int = checkNotNull(savedStateHandle["noteId"])
    private val folderId: Int = checkNotNull(savedStateHandle["folderId"])

    var noteUiState by mutableStateOf(NoteUiState(folderId = folderId))
    private set

    init {
        if (noteId != -1) {
            viewModelScope.launch {
                noteUiState = noteRepository.getNote(noteId)
                    .filterNotNull()
                    .first()
                    .toNoteUiState(actionEnabled = true)
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }



    fun updateUiState(newNoteUiState: NoteUiState) {
        noteUiState = newNoteUiState.copy(actionEnabled = newNoteUiState.isValid())
    }

    suspend fun saveNote() {
        if (noteUiState.isValid()) {
            noteRepository.insertNote(noteUiState.toNote())
        }
    }
    suspend fun updateNote() {
        if (noteUiState.isValid()) {
            noteRepository.updateNote(noteUiState.toNote())
        }
     }
}