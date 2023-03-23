package com.example.organize3.appUi

import com.example.organize3.data.folderWithNotes.Note


data class NoteUiState(
    val id: Int = 0,
    val title: String = "",
    val content: String = "",
    val folderId: Int,
    val actionEnabled: Boolean = false
)
fun NoteUiState.toNote(): Note = Note(
    id = id,
    noteTitle = title,
    folderId = folderId,
    noteContent = content
    )

fun Note.toNoteUiState(actionEnabled: Boolean = false): NoteUiState = NoteUiState(
    id = id,
    title = noteTitle,
    folderId = folderId,
    content = noteContent,
)

fun NoteUiState.isValid(): Boolean {
    return title.isNotBlank() || content.isNotBlank()
}