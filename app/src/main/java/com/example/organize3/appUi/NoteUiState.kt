package com.example.organize3.appUi

import android.net.Uri
import com.example.organize3.data.folderWithNotes.Note


data class NoteUiState(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val uris: List<String> = listOf(),
    val folderId: Int,
    val isArchived: Int = 0,
    val actionEnabled: Boolean = false
)
fun NoteUiState.toNote(): Note = Note(
    id = id,
    noteTitle = title,
    folderId = folderId,
    noteContent = content,
    imageUris = uris,
    isArchived = isArchived
    )

fun Note.toNoteUiState(actionEnabled: Boolean = false): NoteUiState = NoteUiState(
    id = id,
    title = noteTitle,
    folderId = folderId,
    content = noteContent,
    isArchived = isArchived,
    uris = imageUris
)

fun NoteUiState.isValid(): Boolean {
    return title.isNotBlank() || content.isNotBlank()
}