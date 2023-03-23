package com.example.organize3

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.organize3.data.email.EmailAccount
import com.example.organize3.data.email.EmailDao
import com.example.organize3.data.email.EmailRoomDatabase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class EmailDaoTest {

    private var email1 = EmailAccount(1, "Meet Gmail", "meetsonnia", "jkfaf", "fjka")
    private var email2 = EmailAccount(2, "Meet Jio Gmail", "jfka", "jfkakjdk", "jfkafjka")

    private lateinit var emailRoomDatabase: EmailRoomDatabase
    private lateinit var emailDao: EmailDao

    @Before
    fun createDb() {
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        emailRoomDatabase = Room.inMemoryDatabaseBuilder(context, EmailRoomDatabase::class.java)
        // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        emailDao = emailRoomDatabase.emailDao()
    }

    @After
    @kotlin.jvm.Throws(IOException::class)
    fun closeDb() {
        emailRoomDatabase.close()
    }

    private suspend fun addOneEmailToDb() {
        emailDao.insert(email1)
    }

    private suspend fun addTwoEmailToDb() {
        emailDao.insert(email1)
        emailDao.insert(email2)
    }

    @Test
    @kotlin.jvm.Throws(Exception::class)
    fun daoInsert_insertsEmailIntoDB() = runBlocking {
        addOneEmailToDb()
        val allEmails = emailDao.getEmailAccounts().first()
        assertEquals(allEmails[0], email1)
    }

    @Test
    @kotlin.jvm.Throws(Exception::class)
    fun daoGetAllEmails_returnsAllEmailsFromDB() = runBlocking {
        addTwoEmailToDb()
        val allEmails = emailDao.getEmailAccounts().first()
        assertEquals(allEmails[0], email1)
        assertEquals(allEmails[1], email2)
    }

}