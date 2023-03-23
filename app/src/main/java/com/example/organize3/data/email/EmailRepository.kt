package com.example.organize3.data.email

import com.example.organize3.data.email.EmailAccount
import kotlinx.coroutines.flow.Flow


/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface EmailRepository {

    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllEmailsStream(): Flow<List<EmailAccount>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getItemStream(id: Int): Flow<EmailAccount?>

    /**
     * Insert item in the data source
     */
    suspend fun insertEmail(emailAccount: EmailAccount)

    /**
     * Delete item from the data source
     */
    suspend fun deleteEmail(emailAccount: EmailAccount)

    /**
     * Update item in the data source
     */
    suspend fun updateEmail(emailAccount: EmailAccount)


}