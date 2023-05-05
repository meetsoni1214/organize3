package com.example.organize3.data.application

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.organize3.data.application.ApplicationAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface ApplicationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(applicationAccount: ApplicationAccount)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(applicationAccounts: List<ApplicationAccount>)

    @Update
    suspend fun update(applicationAccount: ApplicationAccount)

    @Delete
    suspend fun delete(applicationAccount: ApplicationAccount)

    @Query("SELECT * from application_account WHERE id = :id")
    fun getApplicationAccount(id: Int): Flow<ApplicationAccount>

    @Query("SELECT * from application_account WHERE isArchived = 0 ORDER BY title ASC")
    fun getApplicationAccounts(): Flow<List<ApplicationAccount>>

    @Query("SELECT * from application_account WHERE isArchived = 1 ORDER BY title ASC")
    fun getArchivedApplicationAccounts(): Flow<List<ApplicationAccount>>


}