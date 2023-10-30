package com.example.organize3.data

import android.content.Context
import com.example.organize3.data.application.ApplicationRepository
import com.example.organize3.data.application.ApplicationRoomDatabase
import com.example.organize3.data.application.OfflineApplicationRepository
import com.example.organize3.data.bankAccount.BankAccountRepository
import com.example.organize3.data.bankAccount.BankAccountRoomDatabase
import com.example.organize3.data.bankAccount.OfflineBankAccountRepository
import com.example.organize3.data.folderWithNotes.FolderWithNotesRepository
import com.example.organize3.data.folderWithNotes.FolderWithNotesRoomDatabase
import com.example.organize3.data.folderWithNotes.OfflineFolderWithNotesRepository

interface AppContainer {
//    val emailRepository: EmailRepository
    val applicationRepository: ApplicationRepository
    val bankAccountRepository: BankAccountRepository
    val folderWithNotesRepository: FolderWithNotesRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineEmailRepository], [OfflineApplicationRepository], [BankAccountRepository], [FolderRepository]
 */
class AppDataContainer(private val context: Context): AppContainer {

    /**
     * Implementation for [ApplicationRepository]
     */
    override val applicationRepository: ApplicationRepository by lazy {
        OfflineApplicationRepository(ApplicationRoomDatabase.getDatabase(context).applicationDao())
    }
    /**
     * Implementation for [BankAccountRepository]
     */
    override val bankAccountRepository: BankAccountRepository by lazy {
        OfflineBankAccountRepository(BankAccountRoomDatabase.getDatabase(context).bankAccountDao())
    }
    /**
     * Implementation for [FolderWithNotesRepository]
     */
    override val folderWithNotesRepository: FolderWithNotesRepository by lazy {
        OfflineFolderWithNotesRepository(FolderWithNotesRoomDatabase.getDatabase(context).folderWithNotesDao())
    }

}