package com.example.organize3.data.email

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EmailAccount::class], version = 2, exportSchema = false)
abstract class EmailRoomDatabase: RoomDatabase(){

    abstract fun emailDao(): EmailDao

    companion object {
        @Volatile
        private var Instance: EmailRoomDatabase? = null

        fun getDatabase(context: Context): EmailRoomDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, EmailRoomDatabase::class.java, "email_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}