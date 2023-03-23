package com.example.organize3.data.folderWithNotes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "folderId") val folderId: Int,
    @ColumnInfo(name = "title") val noteTitle: String,
    @ColumnInfo(name = "content") val noteContent: String
)
