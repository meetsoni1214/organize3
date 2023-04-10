package com.example.organize3.data.folderWithNotes

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "folderId") val folderId: Int,
    @ColumnInfo(name = "title") val noteTitle: String,
    @ColumnInfo(name = "content") val noteContent: String,
    @ColumnInfo(name = "uris") val imageUris: List<String>
)
