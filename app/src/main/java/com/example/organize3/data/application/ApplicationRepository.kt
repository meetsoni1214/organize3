package com.example.organize3.data.application

import com.example.organize3.data.application.ApplicationAccount
import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Item] from a given data source.
 */
interface ApplicationRepository {

    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllApplicationsStream(): Flow<List<ApplicationAccount>>

    fun getArchivedApplicationsStream(): Flow<List<ApplicationAccount>>
    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getApplicationStream(id: Int): Flow<ApplicationAccount?>

    /**
     * Insert item in the data source
     */
    suspend fun insertApplication(applicationAccount: ApplicationAccount)

    /**
     * Insert multiple item in the data source
     */
    suspend fun insertApplications(applicationAccounts: List<ApplicationAccount>)

    /**
     * Delete item from the data source
     */
    suspend fun deleteApplication(applicationAccount: ApplicationAccount)

    /**
     * Update item from the data source
     */
    suspend fun updateApplication(applicationAccount: ApplicationAccount)
}
