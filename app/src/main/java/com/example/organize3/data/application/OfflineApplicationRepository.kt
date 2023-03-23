package com.example.organize3.data.application

import com.example.organize3.data.application.ApplicationAccount
import com.example.organize3.data.application.ApplicationDao
import com.example.organize3.data.application.ApplicationRepository
import kotlinx.coroutines.flow.Flow

class OfflineApplicationRepository(private val applicationDao: ApplicationDao):
    ApplicationRepository {
    override fun getAllApplicationsStream(): Flow<List<ApplicationAccount>> {
        return applicationDao.getApplicationAccounts()
    }

    override fun getApplicationStream(id: Int): Flow<ApplicationAccount?> {
        return applicationDao.getApplicationAccount(id)
    }

    override suspend fun insertApplication(applicationAccount: ApplicationAccount) {
        applicationDao.insert(applicationAccount)
    }

    override suspend fun insertApplications(applicationAccounts: List<ApplicationAccount>) {
        applicationDao.insertAll(applicationAccounts)
    }

    override suspend fun deleteApplication(applicationAccount: ApplicationAccount) {
        applicationDao.delete(applicationAccount)
    }

    override suspend fun updateApplication(applicationAccount: ApplicationAccount) {
        applicationDao.update(applicationAccount)
    }

}