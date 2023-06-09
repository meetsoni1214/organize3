package com.example.organize3.notes

import android.graphics.Bitmap
import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.example.organize3.appUi.*
import com.example.organize3.data.folderWithNotes.FolderWithNotes
import com.example.organize3.data.folderWithNotes.FolderWithNotesRepository
import com.example.organize3.data.folderWithNotes.Note
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteEntryViewModel(
        savedStateHandle: SavedStateHandle,
        private val noteRepository: FolderWithNotesRepository
    ): ViewModel() {

        val noteId: Int = checkNotNull(savedStateHandle["noteId"])
    private val folderId: Int = checkNotNull(savedStateHandle["folderId"])
    val isArchived: Int = checkNotNull(savedStateHandle["isArchived"])

    val foldersUiState: StateFlow<FoldersUiState> = noteRepository.getAllFoldersWithNotes().map { FoldersUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = FoldersUiState()
        )

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
    suspend fun archiveNote() {
        noteRepository.updateNote(noteUiState.copy(isArchived = 1).toNote())
    }
    suspend fun unArchiveNote() {
        noteRepository.updateNote(noteUiState.copy(isArchived = 0).toNote())
    }
    suspend fun deleteNote(note: Note) {
        noteRepository.deleteNote(note)
    }

    suspend fun duplicateNote() {
        noteRepository.insertNote(Note(noteTitle = "copy of ${noteUiState.title}", noteContent = noteUiState.content, folderId = noteUiState.folderId, imageUris = noteUiState.uris, isArchived = noteUiState.isArchived))
    }
}

data class FoldersUiState(val folderList: List<FolderWithNotes> = listOf())

