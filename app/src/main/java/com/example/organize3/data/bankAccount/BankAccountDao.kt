package com.example.organize3.data.bankAccount

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.organize3.data.bankAccount.BankAccount
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bankAccount: BankAccount)

    @Update
    suspend fun update(bankAccount: BankAccount)

    @Delete
    suspend fun delete(bankAccount: BankAccount)

    @Query("SELECT * from bankaccount WHERE id = :id")
    fun getBankAccount(id: Int): Flow<BankAccount>

    @Query("SELECT * from bankaccount WHERE isArchived = 0 ORDER BY bank_name ASC")
    fun getBankAccounts(): Flow<List<BankAccount>>

    @Query("SELECT * from bankaccount WHERE isArchived = 1 ORDER BY bank_name ASC")
    fun getArchivedBankAccounts(): Flow<List<BankAccount>>
}