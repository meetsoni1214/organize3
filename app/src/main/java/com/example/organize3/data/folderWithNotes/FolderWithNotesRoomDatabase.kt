package com.example.organize3.data.folderWithNotes

import android.content.Context
import android.net.Uri
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Note::class, Folder::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
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