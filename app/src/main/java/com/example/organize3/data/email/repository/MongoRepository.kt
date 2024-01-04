package com.example.organize3.data.email.repository

import com.example.organize3.data.application.ApplicationAccount
import com.example.organize3.data.email.EmailAccount
import com.example.organize3.util.RequestState
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

typealias EmailAccounts = RequestState<List<EmailAccount>>
typealias ApplicationAccounts = RequestState<List<ApplicationAccount>>
interface MongoRepository {
    fun configureTheRealm()
    fun getAllEmailAccounts(): Flow<EmailAccounts>
    fun getSelectedEmailAccount(emailId: ObjectId): Flow<RequestState<EmailAccount>>
    suspend fun insertEmailAccount(emailAccount: EmailAccount): RequestState<EmailAccount>
    suspend fun updateEmailAccount(emailAccount: EmailAccount): RequestState<EmailAccount>
    suspend fun deleteEmailAccount(id: ObjectId): RequestState<EmailAccount>
    fun getAllApplicationAccounts(): Flow<ApplicationAccounts>
    fun getSelectedApplicationAccount(appId: ObjectId): Flow<RequestState<ApplicationAccount>>
    suspend fun insertApplicationAccount(applicationAccount: ApplicationAccount): RequestState<ApplicationAccount>
    suspend fun updateApplicationAccount(applicationAccount: ApplicationAccount): RequestState<ApplicationAccount>
    suspend fun deleteApplicationAccount(id: ObjectId): RequestState<ApplicationAccount>
}