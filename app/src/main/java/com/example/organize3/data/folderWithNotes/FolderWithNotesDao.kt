package com.example.organize3.data.folderWithNotes

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderWithNotesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFolder(folder: Folder): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNotes(notesList: List<Note>)

    @Delete
    suspend fun deleteNote(note: Note)

    @Delete
    suspend fun deleteFolder(folder: Folder)

    @Update
    suspend fun updateFolder(folder: Folder)

    @Update
    suspend fun updateNote(note: Note)

    @Query("DELETE FROM notes WHERE folderId = :id")
    suspend fun deleteNotes(id: Int)

    @Query("DELETE FROM Folder WHERE id = :id")
    suspend fun deleteFolderWithId(id: Int)

    @Transaction
    @Query("SELECT * FROM Folder WHERE id = :folderId")
    fun getNotesWithFolder(folderId: Int): Flow<FolderWithNotes>

    @Transaction
    @Query("SELECT * FROM Folder")
    fun getNotesWithAllFolder(): Flow<List<FolderWithNotes>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    fun getNote(noteId: Int): Flow<Note>
}