package com.example.organize3.data.folderWithNotes

import kotlinx.coroutines.flow.Flow

interface FolderWithNotesRepository {
    suspend fun insertNote(note: Note)

    suspend fun insertNotes(notesList: List<Note>)

    suspend fun deleteNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun insertFolder(folder: Folder): Long

    suspend fun deleteFolder(folder: Folder)

    suspend fun updateFolder(folder: Folder)

    suspend fun deleteNotes(id: Int)

    suspend fun deleteFolderWithId(id: Int)

    fun getFolderWithNotes(folderId: Int): Flow<FolderWithNotes>

    fun getAllFoldersWithNotes(): Flow<List<FolderWithNotes>>

    fun getNote(noteId: Int): Flow<Note>

}