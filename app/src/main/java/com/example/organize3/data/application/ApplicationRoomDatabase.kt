package com.example.organize3.data.application

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ApplicationAccount::class], version = 2, exportSchema = false)
abstract class ApplicationRoomDatabase: RoomDatabase(){

    abstract fun applicationDao(): ApplicationDao

    companion object {
        @Volatile
        private var Instance: ApplicationRoomDatabase? = null

        fun getDatabase(context: Context): ApplicationRoomDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ApplicationRoomDatabase::class.java, "application_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}