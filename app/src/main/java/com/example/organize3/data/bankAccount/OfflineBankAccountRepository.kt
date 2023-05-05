package com.example.organize3.data.bankAccount


import kotlinx.coroutines.flow.Flow

class OfflineBankAccountRepository(private val bankAccountDao: BankAccountDao):
    BankAccountRepository {
    override fun getAllBankAccountsStream(): Flow<List<BankAccount>> {
        return bankAccountDao.getBankAccounts()
    }

    override fun getArchivedBankAccountsStream(): Flow<List<BankAccount>> {
        return bankAccountDao.getArchivedBankAccounts()
    }

    override fun getBankAccountStream(id: Int): Flow<BankAccount?> {
        return bankAccountDao.getBankAccount(id)
    }

    override suspend fun insertBankAccount(bankAccount: BankAccount) {
        bankAccountDao.insert(bankAccount)
    }

    override suspend fun deleteBankAccount(bankAccount: BankAccount) {
        bankAccountDao.delete(bankAccount)
    }

    override suspend fun updateBankAccount(bankAccount: BankAccount) {
        bankAccountDao.update(bankAccount)
    }
}