package com.example.organize3.data.email.repository

import android.util.Log
import com.example.organize3.data.application.ApplicationAccount
import com.example.organize3.data.email.EmailAccount
import com.example.organize3.util.Constants.APP_ID
import com.example.organize3.util.RequestState
import com.example.organize3.util.UserNotAuthenticatedException
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId


object MongoDB: MongoRepository {
    private val app = App.Companion.create(APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm
    init {
        configureTheRealm()
    }
    override fun configureTheRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(EmailAccount::class, ApplicationAccount::class))
                .initialSubscriptions { sub ->
                    add(
                        query = sub.query<EmailAccount>("ownerId == $0", user.id),
                        name = "User's EmailAccounts"
                    )
                    add(
                        query = sub.query<ApplicationAccount>("ownerId == $0", user.id),
                        name = "User's Application Account"
                    )
                }
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
            Log.d("USERID", "$user")
            Log.d("Realm", realm.subscriptions.errorMessage.toString())
        }
    }

    override fun getAllEmailAccounts(): Flow<EmailAccounts> {
        return if (user != null) {
            try {
                realm.query<EmailAccount>(query = "ownerId == $0 AND isArchived == $1", user.id, 0)
                    .sort(property = "title")
                    .asFlow()
                    .map { result ->
                        RequestState.Success(
                            data = result.list
                        )
                    }
            } catch (e: Exception) {
              flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override fun getSelectedEmailAccount(emailId: ObjectId): Flow<RequestState<EmailAccount>> {
        return if (user != null) {
            try {
                realm.query<EmailAccount>(query = "_id == $0", emailId).asFlow().map {
                    RequestState.Success(data = it.list.first())
                }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override suspend fun insertEmailAccount(emailAccount: EmailAccount): RequestState<EmailAccount> {
        return if (user != null) {
            realm.write {
                try {
                   val addedEmailAccount = copyToRealm(emailAccount.apply { ownerId = user.id })
                    RequestState.Success(data = addedEmailAccount)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun updateEmailAccount(emailAccount: EmailAccount): RequestState<EmailAccount> {
        return if (user != null) {
            realm.write {
                try {
                   val queriedEmailAccount = query<EmailAccount>(query = "_id == $0", emailAccount._id).first().find()
                    if (queriedEmailAccount != null) {
                        queriedEmailAccount.title = emailAccount.title
                        queriedEmailAccount.email = emailAccount.email
                        queriedEmailAccount.password = emailAccount.password
                        queriedEmailAccount.remarks = emailAccount.remarks
                        queriedEmailAccount.isArchived = emailAccount.isArchived
                        RequestState.Success(data = queriedEmailAccount)
                    } else {
                        RequestState.Error(error = Exception("Queried EmailAccount does not exist"))
                    }
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun deleteEmailAccount(id: ObjectId): RequestState<EmailAccount> {
        return if (user != null) {
            realm.write {
                val emailAccount =
                    query<EmailAccount>(query = "_id == $0 AND ownerId == $1", id, user.id)
                        .first().find()
                if (emailAccount != null) {
                    try {
                        delete(emailAccount)
                        RequestState.Success(data = emailAccount)
                    } catch (e: Exception) {
                        RequestState.Error(e)
                    }
                } else {
                    RequestState.Error(Exception("Email Account does not exist.!"))
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override fun getAllApplicationAccounts(): Flow<ApplicationAccounts> {
        return if (user != null) {
            try {
                realm.query<ApplicationAccount>(query = "ownerId == $0 AND isArchived == $1", user.id, 0)
                    .sort(property = "title")
                    .asFlow()
                    .map { result ->
                        RequestState.Success(
                            data = result.list
                        )
                    }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override fun getSelectedApplicationAccount(appId: ObjectId): Flow<RequestState<ApplicationAccount>> {
        return if (user != null) {
            try {
                realm.query<ApplicationAccount>(query = "_id == $0", appId).asFlow().map {
                    RequestState.Success(data = it.list.first())
                }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override suspend fun insertApplicationAccount(applicationAccount: ApplicationAccount): RequestState<ApplicationAccount> {
        return if (user != null) {
            realm.write {
                try {
                    val addedApplicationAccount = copyToRealm(applicationAccount.apply { ownerId = user.id })
                    RequestState.Success(data = addedApplicationAccount)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun updateApplicationAccount(applicationAccount: ApplicationAccount): RequestState<ApplicationAccount> {
        return if (user != null) {
            realm.write {
                try {
                    val queriedApplicationAccount = query<ApplicationAccount>(query = "_id == $0", applicationAccount._id).first().find()
                    if (queriedApplicationAccount != null) {
                        queriedApplicationAccount.title = applicationAccount.title
                        queriedApplicationAccount.username = applicationAccount.username
                        queriedApplicationAccount.password = applicationAccount.password
                        queriedApplicationAccount.remarks = applicationAccount.remarks
                        queriedApplicationAccount.isArchived = applicationAccount.isArchived
                        RequestState.Success(data = queriedApplicationAccount)
                    } else {
                        RequestState.Error(error = Exception("Queried ApplicationAccount does not exist"))
                    }
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun deleteApplicationAccount(id: ObjectId): RequestState<ApplicationAccount> {
        return if (user != null) {
            realm.write {
                val applicationAccount =
                    query<ApplicationAccount>(query = "_id == $0 AND ownerId == $1", id, user.id)
                        .first().find()
                if (applicationAccount != null) {
                    try {
                        delete(applicationAccount)
                        RequestState.Success(data = applicationAccount)
                    } catch (e: Exception) {
                        RequestState.Error(e)
                    }
                } else {
                    RequestState.Error(Exception("Application Account does not exist.!"))
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }
}

