package com.example.organize3.data.bankAccount

import com.example.organize3.data.bankAccount.BankAccount
import kotlinx.coroutines.flow.Flow


/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface BankAccountRepository {

    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllBankAccountsStream(): Flow<List<BankAccount>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getBankAccountStream(id: Int): Flow<BankAccount?>

    /**
     * Insert item in the data source
     */
    suspend fun insertBankAccount(bankAccount: BankAccount)

    /**
     * Delete item from the data source
     */
    suspend fun deleteBankAccount(bankAccount: BankAccount)

    /**
     * Update item in the data source
     */
    suspend fun updateBankAccount(bankAccount: BankAccount)

}