package com.example.organize3.data.bankAccount

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BankAccount::class], version = 3, exportSchema = false)
abstract class BankAccountRoomDatabase: RoomDatabase() {

    abstract fun bankAccountDao(): BankAccountDao

    companion object {
        @Volatile
        private var Instance: BankAccountRoomDatabase? = null

        fun getDatabase(context: Context): BankAccountRoomDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, BankAccountRoomDatabase::class.java, "bankaccount_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        Instance = it
                    }
            }
        }
    }
}