package com.example.organize3.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.folderWithNotes.FolderWithNotesRepository
import com.example.organize3.data.folderWithNotes.Note
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * View Model to retrieve all items in the Room database.
 */
class NotesHomeViewModel (
    private val noteRepository: FolderWithNotesRepository,
    savedStateHandle: SavedStateHandle
        ): ViewModel(){
      val folderId: Int = checkNotNull(savedStateHandle["id"])
    val folderName: String = checkNotNull(savedStateHandle["folderName"])
            val noteHomeUiState: StateFlow<NoteHomeUiState> = noteRepository.getFolderWithNotes(folderId).map { NoteHomeUiState(it.Notes) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = NoteHomeUiState()
                )
            companion object {
                private const val TIMEOUT_MILLIS = 5_000L
            }
    suspend fun deleteNote(note: Note) {
        noteRepository.deleteNote(note)
    }
}

data class NoteHomeUiState(val notesList: List<Note> = listOf())