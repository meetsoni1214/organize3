package com.example.organize3.data.folderWithNotes

import kotlinx.coroutines.flow.Flow

class OfflineFolderWithNotesRepository(private val folderWithNotesDao: FolderWithNotesDao):
    FolderWithNotesRepository {
    override suspend fun insertNote(note: Note) {
        folderWithNotesDao.insertNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        folderWithNotesDao.deleteNote(note)
    }

    override suspend fun updateNote(note: Note) {
        folderWithNotesDao.updateNote(note)
    }

    override suspend fun insertFolder(folder: Folder) {
        folderWithNotesDao.insertFolder(folder)
    }

    override suspend fun deleteFolder(folder: Folder) {
        folderWithNotesDao.deleteFolder(folder)
    }

    override suspend fun updateFolder(folder: Folder) {
        folderWithNotesDao.updateFolder(folder)
    }

    override fun getFolderWithNotes(folderId: Int): Flow<FolderWithNotes> {
        return folderWithNotesDao.getNotesWithFolder(folderId)
    }

    override fun getAllFoldersWithNotes(): Flow<List<FolderWithNotes>> {
        return folderWithNotesDao.getNotesWithAllFolder()
    }

    override fun getNote(noteId: Int): Flow<Note> {
        return folderWithNotesDao.getNote(noteId)
    }

}