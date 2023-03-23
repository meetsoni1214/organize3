package com.example.organize3.data.folderWithNotes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Note::class, Folder::class], version = 1, exportSchema = false)
abstract class FolderWithNotesRoomDatabase: RoomDatabase() {
    abstract fun folderWithNotesDao(): FolderWithNotesDao

    companion object {
        @Volatile
        private var Instance: FolderWithNotesRoomDatabase? = null

        fun getDatabase(context: Context): FolderWithNotesRoomDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, FolderWithNotesRoomDatabase::class.java, "folderWithNotes_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}