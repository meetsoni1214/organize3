package com.example.organize3.data.email

import com.example.organize3.data.email.EmailAccount
import com.example.organize3.data.email.EmailDao
import com.example.organize3.data.email.EmailRepository
import kotlinx.coroutines.flow.Flow

class OfflineEmailRepository(private val emailDao: EmailDao): EmailRepository {
    override fun getAllEmailsStream(): Flow<List<EmailAccount>> {
        return emailDao.getEmailAccounts()
    }

    override fun getItemStream(id: Int): Flow<EmailAccount?> {
        return emailDao.getEmailAccount(id)
    }

    override suspend fun insertEmail(emailAccount: EmailAccount) {
        emailDao.insert(emailAccount)
    }

    override suspend fun deleteEmail(emailAccount: EmailAccount) {
        emailDao.delete(emailAccount)
    }

    override suspend fun updateEmail(emailAccount: EmailAccount) {
        emailDao.update(emailAccount)
    }
}