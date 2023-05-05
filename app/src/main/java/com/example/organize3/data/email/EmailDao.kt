package com.example.organize3.data.email

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.organize3.data.email.EmailAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(emailAccount: EmailAccount)

    @Update
    suspend fun update(emailAccount: EmailAccount)

    @Delete
    suspend fun delete(emailAccount: EmailAccount)

    @Query("SELECT * from emailaccount WHERE id = :id")
    fun getEmailAccount(id: Int): Flow<EmailAccount>

    @Query("SELECT * from emailaccount WHERE isArchived = 0 ORDER BY title ASC")
    fun getEmailAccounts(): Flow<List<EmailAccount>>

    @Query("SELECT * from emailaccount WHERE isArchived = 1 ORDER BY title ASC")
    fun getArchivedEmailAccounts(): Flow<List<EmailAccount>>

}