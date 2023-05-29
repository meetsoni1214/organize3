package com.example.organize3.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.appUi.toNote
import com.example.organize3.data.folderWithNotes.Folder
import com.example.organize3.data.folderWithNotes.FolderWithNotes
import com.example.organize3.data.folderWithNotes.FolderWithNotesRepository
import com.example.organize3.data.folderWithNotes.Note
import kotlinx.coroutines.flow.*

/**
 * View Model to retrieve all items in the Room database.
 */
class NotesHomeViewModel (
    private val noteRepository: FolderWithNotesRepository,
    savedStateHandle: SavedStateHandle
        ): ViewModel(){
      val folderId: Int = checkNotNull(savedStateHandle["id"])
    val folderName: String = checkNotNull(savedStateHandle["folderName"])
//    val folder = noteRepository.getFolderWithNotes(folderId).map { FolderUiState(it) }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//            initialValue = FolderUiState()
//        )
            val noteHomeUiState: StateFlow<NoteHomeUiState> = noteRepository.getFolderWithNotes(folderId).filterNotNull().map { NoteHomeUiState(it.Notes) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                    initialValue = NoteHomeUiState()
                )
            companion object {
                private const val TIMEOUT_MILLIS = 5_000L
            }
    suspend fun archiveNote(note: Note) {
        noteRepository.updateNote(note.copy(isArchived = 1))
    }
    suspend fun undoArchiveNote(note: Note) {
        noteRepository.updateNote(note.copy(isArchived = 0))
    }
    suspend fun deleteFolder(folderId: Int) {
        noteRepository.deleteFolderWithId(folderId)
        noteRepository.deleteNotes(folderId)
    }
}

data class NoteHomeUiState(val notesList: List<Note> = listOf())
//data class FolderUiState(val folderWithNotes: FolderWithNotes = FolderWithNotes())