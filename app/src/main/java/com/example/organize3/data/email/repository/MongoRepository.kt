package com.example.organize3.data.email.repository

import com.example.organize3.data.email.EmailAccount
import com.example.organize3.util.RequestState
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

typealias EmailAccounts = RequestState<List<EmailAccount>>
interface MongoRepository {
    fun configureTheRealm()
    fun getAllEmailAccounts(): Flow<EmailAccounts>
    fun getSelectedEmailAccount(emailId: ObjectId): Flow<RequestState<EmailAccount>>
    suspend fun insertEmailAccount(emailAccount: EmailAccount): RequestState<EmailAccount>
    suspend fun updateEmailAccount(emailAccount: EmailAccount): RequestState<EmailAccount>
    suspend fun deleteDiary(id: ObjectId): RequestState<EmailAccount>
}