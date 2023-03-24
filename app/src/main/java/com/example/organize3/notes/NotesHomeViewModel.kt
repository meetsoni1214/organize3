package com.example.organize3.notes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.folderWithNotes.Folder
import com.example.organize3.data.folderWithNotes.FolderWithNotes
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
//    val folder = noteRepository.getFolderWithNotes(folderId).map { FolderUiState(it) }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
//            initialValue = FolderUiState()
//        )
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
    suspend fun deleteFolder() {
//        noteRepository.deleteFolder(folder)
//        noteRepository.deleteNotes(folderId)
    }
    suspend fun duplicateFolder() {
//        val fId: Int = noteRepository.insertFolder(Folder(folderName = "copy of $folderName")).toInt()
//        for (note in noteHomeUiState.value.notesList) {
//            noteRepository.insertNote(note.copy(folderId = fId))
//        }
    }
}

data class NoteHomeUiState(val notesList: List<Note> = listOf())
//data class FolderUiState(val folderWithNotes: FolderWithNotes = FolderWithNotes())