package com.example.organize3.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organize3.data.folderWithNotes.Folder
import com.example.organize3.data.folderWithNotes.FolderWithNotes
import com.example.organize3.data.folderWithNotes.FolderWithNotesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


/**
 * View Model to retrieve all items in the Room database.
 */
class FolderHomeViewModel(
    private val folderRepository: FolderWithNotesRepository
): ViewModel() {
    val folderHomeUiState: StateFlow<FolderHomeUiState> = folderRepository.getAllFoldersWithNotes().map  {FolderHomeUiState(it)}
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = FolderHomeUiState()
        )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun insertFolder(fName: String) {
        folderRepository.insertFolder(Folder(folderName = fName))
    }
}
/**
 * Ui State for FolderHomeScreen
 */
data class FolderHomeUiState(val folderList: List<FolderWithNotes> = listOf())